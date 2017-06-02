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


ALTER TABLE MERCHANT
	CHANGE COLUMN
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
process_type process_type ENUM('INVOICE','MERCHANT','CRYPTO','TRANSFER') NOT NULL DEFAULT 'MERCHANT' AFTER simple_invoice;

INSERT INTO USER_COMMENT_TOPIC (topic) VALUES ('TRANSFER_CURRENCY_WARNING');

INSERT INTO PHRASE_TEMPLATE (template, topic_id) VALUES ('transfer.warning', '8');

INSERT INTO MERCHANT VALUES (30, 'SimpleTransfer', 'SimpleTransfer', NULL, 2, 'simpleTransferServiceImple', 0, 'TRANSFER', 0, 0, 0, 0);
INSERT INTO MERCHANT VALUES (31, 'VoucherTransfer', 'VoucherTransfer', NULL, 2, 'voucherTransferServiceImple', 0, 'TRANSFER', 0, 0, 0, 0);
INSERT INTO MERCHANT VALUES (32, 'VoucherFreeTransfer', 'VoucherFreeTransfer', NULL, 2, 'voucherFreeTransferServiceImple', 0, 'TRANSFER', 0, 0, 0, 0);

ALTER TABLE MERCHANT_CURRENCY
	ADD COLUMN merchant_transfer_commission DOUBLE(40,9) NULL DEFAULT '0.000000000' AFTER merchant_output_commission;

INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES (30, '/client/img/merchants/transfer.png', 'Transfer', 2);
INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES (31, '/client/img/merchants/voucher.png', 'Voucher', 2);
INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES (32, '/client/img/merchants/voucher_free.png', 'Free voucher', 2);


ALTER TABLE MERCHANT_CURRENCY
	CHANGE COLUMN refill_block refill_block TINYINT(1) NOT NULL DEFAULT '0' AFTER withdraw_block,
	ADD COLUMN transfer_block TINYINT(1) NULL DEFAULT '1' AFTER refill_block;

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, merchant_input_commission, merchant_output_commission, merchant_transfer_commission, withdraw_block, refill_block, transfer_block, merchant_fixed_commission, withdraw_auto_enabled, withdraw_auto_delay_seconds, withdraw_auto_threshold_amount, withdraw_lifetime_hours, refill_lifetime_hours, max_scale_for_refill, max_scale_for_withdraw) VALUES (30, 2, 0.010000000, 0.000000000, 0.500000000, 0.000000000, 1, 1, 0, 0.000000000, 0, 0, 0.000000000, 0, 0, NULL, NULL);
INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, merchant_input_commission, merchant_output_commission, merchant_transfer_commission, withdraw_block, refill_block, transfer_block, merchant_fixed_commission, withdraw_auto_enabled, withdraw_auto_delay_seconds, withdraw_auto_threshold_amount, withdraw_lifetime_hours, refill_lifetime_hours, max_scale_for_refill, max_scale_for_withdraw) VALUES (31, 2, 0.010000000, 0.000000000, 0.500000000, 0.000000000, 1, 1, 0, 0.000000000, 0, 0, 0.000000000, 0, 0, NULL, NULL);
INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, merchant_input_commission, merchant_output_commission, merchant_transfer_commission, withdraw_block, refill_block, transfer_block, merchant_fixed_commission, withdraw_auto_enabled, withdraw_auto_delay_seconds, withdraw_auto_threshold_amount, withdraw_lifetime_hours, refill_lifetime_hours, max_scale_for_refill, max_scale_for_withdraw) VALUES (32, 2, 0.010000000, 0.000000000, 0.500000000, 0.000000000, 1, 1, 0, 0.000000000, 0, 0, 0.000000000, 0, 0, NULL, NULL);


ALTER TABLE MERCHANT_CURRENCY
	ADD COLUMN max_scale_for_transfer INT(11) NULL DEFAULT NULL AFTER max_scale_for_withdraw;

ALTER TABLE CURRENCY
	ADD COLUMN max_scale_for_transfer INT(11) NULL DEFAULT NULL AFTER max_scale_for_withdraw;

UPDATE CURRENCY SET max_scale_for_transfer = max_scale_for_withdraw;