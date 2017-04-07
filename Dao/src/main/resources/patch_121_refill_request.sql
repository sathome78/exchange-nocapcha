ALTER TABLE MERCHANT
	ADD COLUMN simple_invoice TINYINT NULL;
	
UPDATE MERCHANT SET simple_invoice='1' WHERE  name='Invoice';

CREATE TABLE REFILL_REQUEST_STATUS (
	id INT(11) NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NULL DEFAULT NULL,
	PRIMARY KEY (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE REFILL_REQUEST (
	id INT(11) NOT NULL AUTO_INCREMENT,
	amount DECIMAL(40,9) NULL DEFAULT NULL,
	commission DECIMAL(40,9) NULL DEFAULT NULL,
	status_id INT(11) NULL DEFAULT NULL,
	date_creation TIMESTAMP NULL DEFAULT NULL,
	status_modification_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	currency_id INT(11) NULL DEFAULT NULL,
	user_id INT(11) NULL DEFAULT NULL,
	commission_id INT(11) NULL DEFAULT NULL,
	merchant_id INT(11) NULL DEFAULT NULL,
	recipient_bank_code VARCHAR(10) NULL DEFAULT NULL,
	user_full_name VARCHAR(250) NULL DEFAULT NULL,
	remark VARCHAR(300) NULL DEFAULT NULL,
	payer_bank_name VARCHAR(200) NULL DEFAULT NULL,
	payer_bank_code VARCHAR(10) NULL DEFAULT NULL,
	payer_account VARCHAR(100) NULL DEFAULT NULL,
	receipt_scan VARCHAR(100) NULL DEFAULT NULL,
	receipt_scan_name VARCHAR(50) NULL DEFAULT NULL,
	address VARCHAR(64) NULL DEFAULT NULL,
	hash VARCHAR(64) NULL DEFAULT NULL,
	merchant_transaction_id VARCHAR(256) NOT NULL,
	admin_holder_id INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (id),
	INDEX FK_refill_request_refill_request_status (status_id),
	INDEX FK_refill_request_currency (currency_id),
	INDEX FK_refill_request_merchant (merchant_id),
	INDEX FK_refill_request_user (user_id),
	INDEX FK_refill_request_commission (commission_id),
	INDEX FK_refill_request_admin_holder (admin_holder_id),
	CONSTRAINT FK_refill_request_admin_holder FOREIGN KEY (admin_holder_id) REFERENCES USER (id),
	CONSTRAINT FK_refill_request_commission FOREIGN KEY (commission_id) REFERENCES COMMISSION (id),
	CONSTRAINT FK_refill_request_currency FOREIGN KEY (currency_id) REFERENCES CURRENCY (id),
	CONSTRAINT FK_refill_request_merchant FOREIGN KEY (merchant_id) REFERENCES MERCHANT (id),
	CONSTRAINT FK_refill_request_user FOREIGN KEY (user_id) REFERENCES USER (id),
	CONSTRAINT FK_refill_request_refill_request_status FOREIGN KEY (status_id) REFERENCES REFILL_REQUEST_STATUS (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE REFILL_REQUEST_CONFIRMATION (
	id INT(11) NOT NULL AUTO_INCREMENT,
	refill_request_id INT(11) NOT NULL,
	datetime TIMESTAMP NULL DEFAULT NULL,
	confirmation_number INT NOT NULL,
	PRIMARY KEY (id),
	INDEX FK_refill_request_confirmation_refill_request (refill_request_id),
	CONSTRAINT FK_refill_request_confirmation_refill_request FOREIGN KEY (refill_request_id) REFERENCES REFILL_REQUEST (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

