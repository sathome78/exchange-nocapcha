SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS REFILL_REQUEST_PARAM;
DROP TABLE IF EXISTS REFILL_REQUEST;
DROP TABLE IF EXISTS REFILL_REQUEST_ADDRESS;

CREATE TABLE REFILL_REQUEST_ADDRESS (
	id INT(11) NOT NULL AUTO_INCREMENT,
	currency_id INT(11) NOT NULL,
	address VARCHAR(64) NOT NULL,
	user_id INT(11) NOT NULL,
	priv_key VARCHAR(256) NULL DEFAULT NULL,
	pub_key VARCHAR(256) NULL DEFAULT NULL,
	brain_priv_key VARCHAR(256) NULL DEFAULT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX currency_id_address_user_id (currency_id, address, user_id),
	INDEX FK_refill_request_address_user (user_id),
	CONSTRAINT FK_refill_request_address_currency FOREIGN KEY (currency_id) REFERENCES currency (id),
	CONSTRAINT FK_refill_request_address_user FOREIGN KEY (user_id) REFERENCES user (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;



CREATE TABLE REFILL_REQUEST (
	id INT(11) NOT NULL AUTO_INCREMENT,
	amount DECIMAL(40,9) NULL DEFAULT NULL,
	date_creation TIMESTAMP NULL DEFAULT NULL,
	status_id INT(11) NULL DEFAULT NULL,
	status_modification_date TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	currency_id INT(11) NULL DEFAULT NULL,
	user_id INT(11) NULL DEFAULT NULL,
	commission_id INT(11) NULL DEFAULT NULL,
	merchant_id INT(11) NULL DEFAULT NULL,
	recipient_bank_id INT(11) NULL DEFAULT NULL,
	refill_request_address_id INT(11) NULL DEFAULT NULL,
	admin_holder_id INT(11) NULL DEFAULT NULL,
	import_note VARCHAR(50) NULL DEFAULT NULL,
	PRIMARY KEY (id),
	INDEX FK_refill_request_refill_request_new_status (status_id),
	INDEX FK_refill_request_currency (currency_id),
	INDEX FK_refill_request_merchant (merchant_id),
	INDEX FK_refill_request_user (user_id),
	INDEX FK_refill_request_commission (commission_id),
	INDEX FK_refill_request_admin_holder (admin_holder_id),
	INDEX FK_refill_request_invoice_bank (recipient_bank_id),
	INDEX merchant_id_currency_id_status_id_status_modification_date (merchant_id, currency_id, status_id, status_modification_date),
	INDEX status_id_status_modification_date (status_modification_date, status_id),
	INDEX user_id_currency_id_merchant_id (user_id, currency_id, merchant_id),
	INDEX FK_refill_request_refill_request_address (refill_request_address_id),
	CONSTRAINT FK_refill_request_admin_holder FOREIGN KEY (admin_holder_id) REFERENCES user (id),
	CONSTRAINT FK_refill_request_commission FOREIGN KEY (commission_id) REFERENCES commission (id),
	CONSTRAINT FK_refill_request_currency FOREIGN KEY (currency_id) REFERENCES currency (id),
	CONSTRAINT FK_refill_request_invoice_bank FOREIGN KEY (recipient_bank_id) REFERENCES invoice_bank (id),
	CONSTRAINT FK_refill_request_merchant FOREIGN KEY (merchant_id) REFERENCES merchant (id),
	CONSTRAINT FK_refill_request_refill_request_address FOREIGN KEY (refill_request_address_id) REFERENCES refill_request_address (id),
	CONSTRAINT FK_refill_request_refill_request_new_status FOREIGN KEY (status_id) REFERENCES refill_request_status (id),
	CONSTRAINT FK_refill_request_user FOREIGN KEY (user_id) REFERENCES user (id)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;


CREATE TABLE REFILL_REQUEST_PARAM (
	refill_request_id INT(11) NOT NULL,
	param_name ENUM('user_full_name','remark','payer_bank_name','payer_bank_code','payer_account','receipt_scan','receipt_scan_name','merchant_transaction_id') NOT NULL,
	param_value VARCHAR(512) NOT NULL,
	PRIMARY KEY (refill_request_id, param_name),
	UNIQUE INDEX refill_request_id_param_name_param_value (refill_request_id, param_name, param_value),
	CONSTRAINT FK_refill_request_param_refill_request FOREIGN KEY (refill_request_id) REFERENCES refill_request (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;



