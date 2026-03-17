INSERT INTO ccms_ticket (
  grievance_id,
  candidate_name,
  candidate_mobile,
  exam_code,
  category,
  sub_category,
  status,
  source_channel,
  reopen_count,
  created_at,
  updated_at
)
SELECT
  'GRV-2026-0001',
  'Kavya Singh',
  '9876543210',
  'NEET_UG',
  'ADMIT_CARD',
  'DOWNLOAD_ISSUE',
  'NEW',
  'CALL',
  0,
  NOW(),
  NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM ccms_ticket WHERE grievance_id = 'GRV-2026-0001'
);
