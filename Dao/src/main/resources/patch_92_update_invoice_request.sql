ALTER TABLE INVOICE_REQUEST ADD COLUMN `invoice_request_status_id` INT NOT NULL;

UPDATE INVOICE_REQUEST SET invoice_request_status_id = 4 WHERE invoice_request_status_id = 0 AND acceptance_time IS NOT NULL;
UPDATE INVOICE_REQUEST SET invoice_request_status_id = 1 WHERE invoice_request_status_id = 0 AND payer_bank_name IS NULL;
UPDATE INVOICE_REQUEST SET invoice_request_status_id = 2 WHERE invoice_request_status_id = 0 AND payer_bank_name IS NOT NULL;

ALTER TABLE INVOICE_REQUEST
	ADD CONSTRAINT `FK_invoice_request_invoice_request_status` FOREIGN KEY (`invoice_request_status_id`) REFERENCES INVOICE_REQUEST_STATUS (`id`);

ALTER TABLE TRANSACTION
	CHANGE COLUMN `source_type` `source_type` ENUM('ORDER','MERCHANT','REFERRAL','ACCRUAL','MANUAL','USER_TRANSFER','INVOICE') NULL DEFAULT NULL;