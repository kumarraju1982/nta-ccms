param(
  [string]$TenantId = "pb.amritsar",
  [string]$WorkflowBaseUrl = "http://localhost:8085/workflow",
  [string]$WorkflowProcessCode = "CCMS_TICKET",
  [string]$PostgresContainer = "postgres"
)

$ErrorActionPreference = "Stop"

function Invoke-DigitJson {
  param(
    [string]$Method,
    [string]$Url,
    [hashtable]$Headers,
    [object]$Body = $null
  )
  if ($null -eq $Body) {
    return Invoke-RestMethod -Method $Method -Uri $Url -Headers $Headers -TimeoutSec 30
  }
  $json = $Body | ConvertTo-Json -Depth 10
  return Invoke-RestMethod -Method $Method -Uri $Url -Headers $Headers -ContentType "application/json" -Body $json -TimeoutSec 30
}

Write-Host "Seeding MDMS masters in container '$PostgresContainer'..."
$sql = @"
INSERT INTO eg_mdms_data (id, tenantid, uniqueidentifier, schemacode, data, isactive, createdby, lastmodifiedby, createdtime, lastmodifiedtime)
VALUES
  ('ccms-exam-jee-main', '$TenantId', 'JEE_MAIN', 'ccms.exams', '{"code":"JEE_MAIN","name":"JEE Main"}'::jsonb, true, 'system', 'system', (extract(epoch from now())*1000)::bigint, (extract(epoch from now())*1000)::bigint),
  ('ccms-exam-neet-ug', '$TenantId', 'NEET_UG', 'ccms.exams', '{"code":"NEET_UG","name":"NEET UG"}'::jsonb, true, 'system', 'system', (extract(epoch from now())*1000)::bigint, (extract(epoch from now())*1000)::bigint),
  ('ccms-cat-admit-card', '$TenantId', 'ADMIT_CARD', 'ccms.queryCategories', '{"code":"ADMIT_CARD","name":"Admit Card"}'::jsonb, true, 'system', 'system', (extract(epoch from now())*1000)::bigint, (extract(epoch from now())*1000)::bigint),
  ('ccms-cat-payment', '$TenantId', 'PAYMENT', 'ccms.queryCategories', '{"code":"PAYMENT","name":"Payment"}'::jsonb, true, 'system', 'system', (extract(epoch from now())*1000)::bigint, (extract(epoch from now())*1000)::bigint)
ON CONFLICT (tenantid, schemacode, uniqueidentifier)
DO UPDATE SET data = EXCLUDED.data, isactive = true, lastmodifiedby = 'system', lastmodifiedtime = (extract(epoch from now())*1000)::bigint;
"@

$sql | docker exec -i $PostgresContainer psql -U postgres -d postgres | Out-Null
Write-Host "MDMS seed upsert done."

$headers = @{ "X-Tenant-ID" = $TenantId }
$processUrl = "$WorkflowBaseUrl/v1/process?code=$WorkflowProcessCode"
$existing = Invoke-DigitJson -Method "GET" -Url $processUrl -Headers $headers

$process = $null
if ($existing -is [array] -and $existing.Count -gt 0) {
  $process = $existing[0]
  Write-Host "Workflow process '$WorkflowProcessCode' already exists: $($process.id)"
} else {
  Write-Host "Creating workflow process '$WorkflowProcessCode'..."
  $process = Invoke-DigitJson -Method "POST" -Url "$WorkflowBaseUrl/v1/process" -Headers $headers -Body @{
    name = "NTA CCMS Ticket Workflow"
    code = $WorkflowProcessCode
    description = "Workflow for NTA Candidate tickets"
    version = "1.0"
    sla = 1440
  }
  Write-Host "Created process: $($process.id)"
}

$processId = $process.id
if ([string]::IsNullOrWhiteSpace($processId)) {
  throw "Workflow process id is empty for code $WorkflowProcessCode"
}

$existingStates = Invoke-DigitJson -Method "GET" -Url "$WorkflowBaseUrl/v1/process/$processId/state" -Headers $headers
$stateByCode = @{}
if ($existingStates -is [array]) {
  foreach ($s in $existingStates) {
    if ($s.code) { $stateByCode[$s.code] = $s }
  }
}

$requiredStates = @(
  @{ code = "NEW"; name = "New"; isInitial = $true },
  @{ code = "IN_PROGRESS"; name = "In Progress"; isInitial = $false },
  @{ code = "PENDING_INFO_FROM_CANDIDATE"; name = "Pending Info from Candidate"; isInitial = $false },
  @{ code = "RESOLVED_BY_AGENT"; name = "Resolved by Agent"; isInitial = $false },
  @{ code = "UNRESOLVED"; name = "Unresolved"; isInitial = $false },
  @{ code = "ESCALATED_TO_OFFICER"; name = "Escalated to Officer"; isInitial = $false },
  @{ code = "UNDER_OFFICER_REVIEW"; name = "Under Officer Review"; isInitial = $false },
  @{ code = "FINAL_RESOLVED"; name = "Final Resolved"; isInitial = $false },
  @{ code = "REOPENED"; name = "Reopened"; isInitial = $false },
  @{ code = "CLOSED"; name = "Closed"; isInitial = $false }
)

foreach ($s in $requiredStates) {
  if (-not $stateByCode.ContainsKey($s.code)) {
    $created = Invoke-DigitJson -Method "POST" -Url "$WorkflowBaseUrl/v1/process/$processId/state" -Headers $headers -Body @{
      code = $s.code
      name = $s.name
      description = $s.name
      processId = $processId
      sla = 1440
      isInitial = $s.isInitial
    }
    $stateByCode[$created.code] = $created
    Write-Host "Created state $($created.code)"
  }
}

$states = Invoke-DigitJson -Method "GET" -Url "$WorkflowBaseUrl/v1/process/$processId/state" -Headers $headers
$stateIdByCode = @{}
foreach ($s in $states) {
  $stateIdByCode[$s.code] = $s.id
}

function Ensure-Action {
  param(
    [string]$FromCode,
    [string]$ActionName,
    [string]$ToCode,
    [string]$Label
  )
  $fromStateId = $stateIdByCode[$FromCode]
  $toStateId = $stateIdByCode[$ToCode]
  if ([string]::IsNullOrWhiteSpace($fromStateId) -or [string]::IsNullOrWhiteSpace($toStateId)) {
    throw "Missing state id for action $ActionName ($FromCode -> $ToCode)"
  }

  $existingActions = Invoke-DigitJson -Method "GET" -Url "$WorkflowBaseUrl/v1/state/$fromStateId/action" -Headers $headers
  $exists = $false
  if ($existingActions -is [array]) {
    foreach ($a in $existingActions) {
      if ($a.name -eq $ActionName) {
        $exists = $true
        break
      }
    }
  }

  if (-not $exists) {
    Invoke-DigitJson -Method "POST" -Url "$WorkflowBaseUrl/v1/state/$fromStateId/action" -Headers $headers -Body @{
      name = $ActionName
      label = $Label
      currentState = $fromStateId
      nextState = $toStateId
    } | Out-Null
    Write-Host "Created action $ActionName ($FromCode -> $ToCode)"
  }
}

Ensure-Action -FromCode "NEW" -ActionName "NEW" -ToCode "NEW" -Label "Keep New"
Ensure-Action -FromCode "NEW" -ActionName "IN_PROGRESS" -ToCode "IN_PROGRESS" -Label "Start Progress"
Ensure-Action -FromCode "NEW" -ActionName "CLOSED" -ToCode "CLOSED" -Label "Close"
Ensure-Action -FromCode "IN_PROGRESS" -ActionName "PENDING_INFO_FROM_CANDIDATE" -ToCode "PENDING_INFO_FROM_CANDIDATE" -Label "Ask Candidate Info"
Ensure-Action -FromCode "IN_PROGRESS" -ActionName "RESOLVED_BY_AGENT" -ToCode "RESOLVED_BY_AGENT" -Label "Resolve by Agent"
Ensure-Action -FromCode "IN_PROGRESS" -ActionName "UNRESOLVED" -ToCode "UNRESOLVED" -Label "Mark Unresolved"
Ensure-Action -FromCode "PENDING_INFO_FROM_CANDIDATE" -ActionName "IN_PROGRESS" -ToCode "IN_PROGRESS" -Label "Resume Processing"
Ensure-Action -FromCode "PENDING_INFO_FROM_CANDIDATE" -ActionName "CLOSED" -ToCode "CLOSED" -Label "Close"
Ensure-Action -FromCode "RESOLVED_BY_AGENT" -ActionName "CLOSED" -ToCode "CLOSED" -Label "Close"
Ensure-Action -FromCode "RESOLVED_BY_AGENT" -ActionName "REOPENED" -ToCode "REOPENED" -Label "Reopen"
Ensure-Action -FromCode "UNRESOLVED" -ActionName "ESCALATED_TO_OFFICER" -ToCode "ESCALATED_TO_OFFICER" -Label "Escalate to Officer"
Ensure-Action -FromCode "ESCALATED_TO_OFFICER" -ActionName "UNDER_OFFICER_REVIEW" -ToCode "UNDER_OFFICER_REVIEW" -Label "Start Officer Review"
Ensure-Action -FromCode "UNDER_OFFICER_REVIEW" -ActionName "FINAL_RESOLVED" -ToCode "FINAL_RESOLVED" -Label "Final Resolve"
Ensure-Action -FromCode "UNDER_OFFICER_REVIEW" -ActionName "PENDING_INFO_FROM_CANDIDATE" -ToCode "PENDING_INFO_FROM_CANDIDATE" -Label "Request Candidate Info"
Ensure-Action -FromCode "FINAL_RESOLVED" -ActionName "CLOSED" -ToCode "CLOSED" -Label "Close"
Ensure-Action -FromCode "FINAL_RESOLVED" -ActionName "REOPENED" -ToCode "REOPENED" -Label "Reopen"
Ensure-Action -FromCode "REOPENED" -ActionName "IN_PROGRESS" -ToCode "IN_PROGRESS" -Label "Resume In Progress"

Write-Host "DIGIT local bootstrap completed."
