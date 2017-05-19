ALTER TABLE REFILL_REQUEST
  ADD COLUMN inner_transfer_hash VARCHAR(200) NULL;

ALTER TABLE MERCHANT
  ADD COLUMN to_main_account_transferring_confirm_needed BOOLEAN DEFAULT FALSE;

ALTER TABLE MERCHANT
  ADD COLUMN withdraw_transferring_confirm_needed BOOLEAN DEFAULT FALSE;

ALTER TABLE MERCHANT
  ADD COLUMN generate_additional_refill_address_available BOOLEAN DEFAULT FALSE;

ALTER TABLE MERCHANT
  ADD COLUMN additional_tag_for_withdraw_address_is_used BOOLEAN DEFAULT FALSE;
  
ALTER TABLE WITHDRAW_REQUEST
	ADD COLUMN destination_tag VARCHAR(100) NULL DEFAULT NULL AFTER admin_holder_id,
	ADD COLUMN transaction_hash VARCHAR(200) NULL DEFAULT NULL;

/*=========================================*/
ALTER TABLE REFILL_REQUEST_ADDRESS
	ADD COLUMN merchant_id INT(11) NOT NULL AFTER currency_id;

SELECT RRA.*
FROM REFILL_REQUEST_ADDRESS RRA
LEFT JOIN REFILL_REQUEST RR ON RR.refill_request_address_id = RRA.id
WHERE RR.id IS NULL

DELETE RRA
FROM REFILL_REQUEST_ADDRESS RRA
LEFT JOIN REFILL_REQUEST RR ON RR.refill_request_address_id = RRA.id
WHERE RR.id IS NULL;

UPDATE REFILL_REQUEST_ADDRESS
SET merchant_id = (SELECT merchant_id FROM REFILL_REQUEST where refill_request_address_id = REFILL_REQUEST_ADDRESS.id LIMIT 1);

ALTER TABLE REFILL_REQUEST
	DROP FOREIGN KEY FK_refill_request_refill_request_address_2;

ALTER TABLE REFILL_REQUEST_ADDRESS
	DROP INDEX id_currency_id_user_id;

ALTER TABLE REFILL_REQUEST
	DROP INDEX FK_refill_request_refill_request_address_2;

ALTER TABLE REFILL_REQUEST_ADDRESS
	ADD INDEX currency_id_merchant_id_user_id_id (currency_id, merchant_id, user_id, id);

ALTER TABLE REFILL_REQUEST
	ADD CONSTRAINT FK_refill_request_refill_request_address_2 FOREIGN KEY (currency_id, merchant_id, user_id, refill_request_address_id) REFERENCES refill_request_address (currency_id, merchant_id, user_id, id);

/*=========================================*/


ALTER TABLE REFILL_REQUEST_ADDRESS
	ADD COLUMN date_generation TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP AFTER brain_priv_key;

ALTER TABLE WITHDRAW_REQUEST DROP COLUMN transaction_id;

