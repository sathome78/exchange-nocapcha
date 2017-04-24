ALTER TABLE WITHDRAW_REQUEST
	DROP FOREIGN KEY withdraw_request_ibfk_3;
	
ALTER TABLE WITHDRAW_REQUEST
  DROP COLUMN merchant_image_id;

ALTER TABLE MERCHANT
	ADD COLUMN simple_invoice TINYINT NOT NULL DEFAULT '0';


UPDATE MERCHANT SET simple_invoice='1' WHERE  name='Invoice';

CREATE TABLE REFILL_REQUEST_STATUS (
	id INT(11) NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NULL DEFAULT NULL,
	PRIMARY KEY (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (1, 'CREATED_USER');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (2, 'CREATED_BY_FACT');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (3, 'WAITING_CONFIRMATION_USER');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (4, 'ON_PENDING');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (5, 'CONFIRMED_USER');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (6, 'ON_BCH_EXAM');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (7, 'IN_WORK_OF_ADMIN');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (8, 'DECLINED_ADMIN');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (9, 'ACCEPTED_AUTO');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (10, 'ACCEPTED_ADMIN');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (11, 'REVOKED_USER');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (12, 'EXPIRED');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (13, 'TAKEN_FROM_PENDING');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (14, 'TAKEN_FROM_EXAM');


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
	recipient_bank_id INT(11) NULL DEFAULT NULL,
	user_full_name VARCHAR(250) NULL DEFAULT NULL,
	remark VARCHAR(300) NULL DEFAULT NULL,
	payer_bank_name VARCHAR(200) NULL DEFAULT NULL,
	payer_bank_code VARCHAR(10) NULL DEFAULT NULL,
	payer_account VARCHAR(100) NULL DEFAULT NULL,
	receipt_scan VARCHAR(100) NULL DEFAULT NULL,
	receipt_scan_name VARCHAR(50) NULL DEFAULT NULL,
	address VARCHAR(64) NULL DEFAULT NULL,
	wif_priv_key VARCHAR(256) DEFAULT NULL,
	pub_key VARCHAR(256) DEFAULT NULL,
	brain_priv_key VARCHAR(256) DEFAULT NULL,
	hash VARCHAR(64) NULL DEFAULT NULL,
	merchant_transaction_id VARCHAR(256) NULL DEFAULT NULL,
	admin_holder_id INT(11) NULL DEFAULT NULL,
	import_note VARCHAR(50) NULL DEFAULT NULL,
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

ALTER TABLE REFILL_REQUEST
	ADD CONSTRAINT FK_refill_request_invoice_bank FOREIGN KEY (recipient_bank_id) REFERENCES INVOICE_BANK (id);

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

ALTER TABLE MERCHANT_CURRENCY
	ADD COLUMN withdraw_lifetime_hours INT NOT NULL DEFAULT '0.000000000',
	ADD COLUMN refill_lifetime_hours INT NOT NULL DEFAULT '0.000000000';
	
ALTER TABLE REFILL_REQUEST
	ADD INDEX status_id_status_modification_date_currency_id_merchant_id (merchant_id, currency_id, status_modification_date, status_id);

ALTER TABLE REFILL_REQUEST
	ADD INDEX status_id_status_modification_date (status_modification_date, status_id);
	
ALTER TABLE REFILL_REQUEST
	ADD INDEX currency_id_merchant_id_address (currency_id, merchant_id, address);

ALTER TABLE TRANSACTION
	CHANGE COLUMN source_type source_type ENUM('ORDER','MERCHANT','REFERRAL','ACCRUAL','MANUAL','USER_TRANSFER','INVOICE','BTC_INVOICE','WITHDRAW','REFILL') NULL DEFAULT NULL;

