CREATE TABLE IF NOT EXISTS ccms_ticket (
  id BIGSERIAL PRIMARY KEY,
  grievance_id VARCHAR(32) NOT NULL UNIQUE,
  candidate_name VARCHAR(120) NOT NULL,
  candidate_mobile VARCHAR(20) NOT NULL,
  exam_code VARCHAR(64) NOT NULL,
  category VARCHAR(64) NOT NULL,
  sub_category VARCHAR(64) NOT NULL,
  status VARCHAR(40) NOT NULL,
  assigned_agent VARCHAR(80),
  assigned_officer VARCHAR(80),
  source_channel VARCHAR(32) NOT NULL DEFAULT 'CALL',
  reopen_count INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS ccms_ticket_history (
  id BIGSERIAL PRIMARY KEY,
  ticket_id BIGINT NOT NULL REFERENCES ccms_ticket(id) ON DELETE CASCADE,
  grievance_id VARCHAR(32) NOT NULL,
  from_status VARCHAR(40) NOT NULL,
  to_status VARCHAR(40) NOT NULL,
  action_type VARCHAR(32) NOT NULL,
  action_by VARCHAR(80) NOT NULL,
  remarks VARCHAR(400),
  created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ccms_ticket_status_agent
  ON ccms_ticket(status, assigned_agent);

CREATE INDEX IF NOT EXISTS idx_ccms_ticket_status_officer
  ON ccms_ticket(status, assigned_officer);

CREATE INDEX IF NOT EXISTS idx_ccms_ticket_exam_category_created
  ON ccms_ticket(exam_code, category, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_ccms_ticket_updated
  ON ccms_ticket(updated_at DESC);

CREATE INDEX IF NOT EXISTS idx_ccms_ticket_history_grievance_created
  ON ccms_ticket_history(grievance_id, created_at ASC);
