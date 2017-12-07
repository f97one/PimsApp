-- Add finish flag
ALTER TABLE STATUS_MASTER ADD COLUMN TREAT_AS_FINISHED boolean default false;

-- Set default finish flags
update STATUS_MASTER set TREAT_AS_FINISHED = false where STATUS_ID <= 3;
update STATUS_MASTER set TREAT_AS_FINISHED = true where STATUS_ID >= 4;
