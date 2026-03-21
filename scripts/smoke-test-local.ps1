param(
  [string]$BaseAuthUrl = "http://localhost:8081",
  [string]$BaseMasterUrl = "http://localhost:8082",
  [string]$BaseTicketUrl = "http://localhost:8087",
  [string]$Username = "admin",
  [string]$Password = "admin"
)

$ErrorActionPreference = "Stop"

function Step-Info {
  param([string]$Message)
  Write-Host "[INFO] $Message"
}

function Step-Pass {
  param([string]$Message)
  Write-Host "[PASS] $Message"
}

function Step-Fail {
  param([string]$Message)
  Write-Host "[FAIL] $Message"
}

function Invoke-Json {
  param(
    [string]$Method,
    [string]$Url,
    [object]$Body = $null,
    [hashtable]$Headers = $null
  )

  if ($null -eq $Headers) {
    $Headers = @{}
  }

  if ($null -eq $Body) {
    return Invoke-RestMethod -Method $Method -Uri $Url -Headers $Headers -TimeoutSec 30
  }

  $json = $Body | ConvertTo-Json -Depth 10
  return Invoke-RestMethod -Method $Method -Uri $Url -Headers $Headers -ContentType "application/json" -Body $json -TimeoutSec 30
}

$failed = $false
$grievanceId = $null

try {
  Step-Info "Health check: ticket service"
  $health = Invoke-Json -Method "GET" -Url "$BaseTicketUrl/api/v1/health"
  if ("$health" -eq "ok") {
    Step-Pass "Ticket service is reachable"
  } else {
    Step-Fail "Unexpected health response: $health"
    $failed = $true
  }
} catch {
  Step-Fail "Ticket health failed: $($_.Exception.Message)"
  $failed = $true
}

try {
  Step-Info "Auth login"
  $login = Invoke-Json -Method "POST" -Url "$BaseAuthUrl/api/v1/auth/login/password" -Body @{
    username = $Username
    password = $Password
  }
  $token = $login.token
  if (-not $token) { $token = $login.accessToken }
  if ([string]::IsNullOrWhiteSpace($token)) {
    Step-Fail "Login succeeded but no token returned"
    $failed = $true
  } else {
    Step-Pass "Login succeeded"
  }
} catch {
  Step-Fail "Auth login failed: $($_.Exception.Message)"
  $failed = $true
}

try {
  Step-Info "Master fetch: exams"
  $exams = Invoke-Json -Method "GET" -Url "$BaseMasterUrl/api/v1/masters/exams"
  Step-Pass "Exams fetched (count=$($exams.Count))"
} catch {
  Step-Fail "Exams fetch failed: $($_.Exception.Message)"
  $failed = $true
}

try {
  Step-Info "Master fetch: categories"
  $categories = Invoke-Json -Method "GET" -Url "$BaseMasterUrl/api/v1/masters/categories"
  Step-Pass "Categories fetched (count=$($categories.Count))"
} catch {
  Step-Fail "Categories fetch failed: $($_.Exception.Message)"
  $failed = $true
}

try {
  Step-Info "Create ticket"
  $created = Invoke-Json -Method "POST" -Url "$BaseTicketUrl/api/v1/tickets" -Body @{
    candidateName = "Smoke Candidate"
    candidateMobile = "9000000001"
    examCode = "JEE_MAIN"
    category = "ADMIT_CARD"
    subCategory = "DOWNLOAD_ISSUE"
    transcript = "Candidate unable to download admit card"
    sourceChannel = "CALL"
  }
  $grievanceId = $created.grievanceId
  if ([string]::IsNullOrWhiteSpace($grievanceId)) {
    Step-Fail "Ticket created but grievanceId missing"
    $failed = $true
  } else {
    Step-Pass "Ticket created ($grievanceId)"
  }
} catch {
  Step-Fail "Ticket create failed: $($_.Exception.Message)"
  $failed = $true
}

if ($grievanceId) {
  try {
    Step-Info "Transition ticket to IN_PROGRESS"
    $transition = Invoke-Json -Method "POST" -Url "$BaseTicketUrl/api/v1/tickets/$grievanceId/transition" -Body @{
      toStatus = "IN_PROGRESS"
      actionBy = "agent1"
      remarks = "Smoke transition"
    }
    if ($transition.status -eq "IN_PROGRESS") {
      Step-Pass "Transition succeeded (IN_PROGRESS)"
    } else {
      Step-Fail "Transition returned unexpected status: $($transition.status)"
      $failed = $true
    }
  } catch {
    Step-Fail "Transition failed: $($_.Exception.Message)"
    $failed = $true
  }

  try {
    Step-Info "Fetch ticket history"
    $history = Invoke-Json -Method "GET" -Url "$BaseTicketUrl/api/v1/tickets/$grievanceId/history"
    if ($history.Count -ge 2) {
      Step-Pass "History fetched (count=$($history.Count))"
    } else {
      Step-Fail "History count is too low (count=$($history.Count))"
      $failed = $true
    }
  } catch {
    Step-Fail "History fetch failed: $($_.Exception.Message)"
    $failed = $true
  }
}

if ($failed) {
  Write-Host ""
  Write-Host "SMOKE RESULT: FAILED"
  exit 1
}

Write-Host ""
Write-Host "SMOKE RESULT: PASSED"
exit 0
