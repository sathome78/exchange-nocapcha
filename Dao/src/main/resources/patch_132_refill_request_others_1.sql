ALTER TABLE MERCHANT
  DROP COLUMN simple_invoice;

ALTER TABLE MERCHANT
	DROP COLUMN to_main_account_transferring_confirm_needed;

ALTER TABLE MERCHANT
	DROP COLUMN generate_additional_refill_address_available;

ALTER TABLE MERCHANT
	DROP COLUMN withdraw_transferring_confirm_needed;

