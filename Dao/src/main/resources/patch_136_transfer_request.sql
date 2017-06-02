CREATE TABLE TRANSFER_REQUEST_STATUS (
	id INT(11) NOT NULL AUTO_INCREMENT,
	name VARCHAR(100) NULL DEFAULT NULL,
	PRIMARY KEY (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO TRANSFER_REQUEST_STATUS VALUES (1, 'CREATED_USER');
INSERT INTO TRANSFER_REQUEST_STATUS VALUES (2, 'POSTED');
INSERT INTO TRANSFER_REQUEST_STATUS VALUES (3, 'REVOKED');
INSERT INTO TRANSFER_REQUEST_STATUS VALUES (4, 'POSTPONED_AS_VOUCHER');


CREATE TABLE TRANSFER_REQUEST (
	id INT(11) NOT NULL AUTO_INCREMENT,
	amount DECIMAL(40,9) NULL DEFAULT NULL,
	commission DECIMAL(40,9) NULL DEFAULT NULL,
	date_creation TIMESTAMP NULL DEFAULT NULL,
	status_id INT(11) NULL DEFAULT NULL,
	status_modification_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	merchant_id INT(11) NULL,
	currency_id INT(11) NULL DEFAULT NULL,
	user_id INT(11) NOT NULL,
	commission_id INT(11) NULL DEFAULT NULL,
	recipient_user_id INT(11) NULL DEFAULT NULL,
	hash VARCHAR(256) NULL DEFAULT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX hash (hash),
	INDEX FK_transfer_request_user (user_id),
	INDEX FK_transfer_request_user_2 (recipient_user_id),
	INDEX FK_transfer_request_commission (commission_id),
	INDEX FK_transfer_request_currency (currency_id),
	INDEX FK_transfer_request_merchant (merchant_id),
	INDEX FK_transfer_request_transfer_request_status (status_id),
	CONSTRAINT FK_transfer_request_commission FOREIGN KEY (commission_id) REFERENCES COMMISSION (id),
	CONSTRAINT FK_transfer_request_currency FOREIGN KEY (currency_id) REFERENCES CURRENCY (id),
	CONSTRAINT FK_transfer_request_merchant FOREIGN KEY (merchant_id) REFERENCES MERCHANT (id),
	CONSTRAINT FK_transfer_request_transfer_request_status FOREIGN KEY (status_id) REFERENCES TRANSFER_REQUEST_STATUS (id),
	CONSTRAINT FK_transfer_request_user FOREIGN KEY (user_id) REFERENCES USER (id),
	CONSTRAINT FK_transfer_request_user_2 FOREIGN KEY (recipient_user_id) REFERENCES USER (id)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

ALTER TABLE MERCHANT
	CHANGE COLUMN process_type process_type ENUM('INVOICE','MERCHANT','CRYPTO','TRANSFER') NOT NULL DEFAULT 'MERCHANT' AFTER simple_invoice;

INSERT INTO USER_COMMENT_TOPIC (topic) VALUES ('TRANSFER_CURRENCY_WARNING');

INSERT INTO PHRASE_TEMPLATE (template, topic_id) VALUES ('transfer.warning', '8');

