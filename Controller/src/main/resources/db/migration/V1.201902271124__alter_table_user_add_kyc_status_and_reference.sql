ALTER TABLE USER
  DROP COLUMN kyc_reference;

ALTER TABLE USER
  ADD COLUMN kyc_status VARCHAR(55) NOT NULL DEFAULT 'none',
  ADD COLUMN kyc_reference VARCHAR(55);