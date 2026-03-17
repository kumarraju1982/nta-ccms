ALTER TABLE ccms_ticket
  ADD COLUMN IF NOT EXISTS workflow_process_id VARCHAR(80),
  ADD COLUMN IF NOT EXISTS workflow_instance_id VARCHAR(120);
