ALTER TABLE MERCHANT
  DROP COLUMN simple_invoice;

ALTER TABLE MERCHANT
	DROP COLUMN to_main_account_transferring_confirm_needed;

ALTER TABLE MERCHANT
	DROP COLUMN generate_additional_refill_address_available;

ALTER TABLE MERCHANT
	DROP COLUMN withdraw_transferring_confirm_needed;

ALTER TABLE MERCHANT
	DROP COLUMN additional_tag_for_withdraw_address_is_used;

ALTER TABLE REFILL_REQUEST_PARAM
	ADD COLUMN merchant_request_sign VARCHAR(256) NULL DEFAULT NULL;