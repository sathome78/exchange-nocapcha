ALTER TABLE INVOICE_REQUEST
	ADD COLUMN status_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE INVOICE_REQUEST
	ADD INDEX invoice_request_status_id_status_update_date (status_update_date, invoice_request_status_id);

