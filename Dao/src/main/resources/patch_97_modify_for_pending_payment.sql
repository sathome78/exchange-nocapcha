CREATE TABLE PENDING_PAYMENT_STATUS (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO PENDING_PAYMENT_STATUS (id, name)
VALUES 
(1, "CREATED_USER"),
(2, "REVOKED_USER"),
(3, "ACCEPTED_AUTO"),
(4, "ACCEPTED_ADMIN"),
(5, "EXPIRED"),
(6, "ON_BCH_EXAM");

ALTER TABLE PENDING_PAYMENT ADD COLUMN `pending_payment_status_id` INT NOT NULL;

-- TODO
-- UPDATE PENDING_PAYMENT SET pending_payment_status_id = 4 WHERE pending_payment_status_id = 0 AND acceptance_time IS NOT NULL;
-- UPDATE PENDING_PAYMENT SET pending_payment_status_id = 1 WHERE pending_payment_status_id = 0 AND payer_bank_name IS NULL;
-- UPDATE PENDING_PAYMENT SET pending_payment_status_id = 2 WHERE pending_payment_status_id = 0 AND payer_bank_name IS NOT NULL;

ALTER TABLE PENDING_PAYMENT
	ADD CONSTRAINT `FK_pending_payment_pending_payment_status` FOREIGN KEY (`pending_payment_status_id`) REFERENCES PENDING_PAYMENT_STATUS (`id`);


ALTER TABLE PENDING_PAYMENT
	ADD COLUMN status_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE PENDING_PAYMENT
	ADD INDEX pending_payment_status_id_status_update_date (status_update_date, pending_payment_status_id);

ALTER TABLE PENDING_PAYMENT
  ADD COLUMN acceptance_user_id INT NULL;

ALTER TABLE PENDING_PAYMENT
  ADD CONSTRAINT FK_pending_payment_user FOREIGN KEY (acceptance_user_id) REFERENCES USER (`id`);

ALTER TABLE PENDING_PAYMENT
  ADD COLUMN acceptance_time DATETIME NULL DEFAULT NULL;


ALTER TABLE PENDING_PAYMENT
  ADD COLUMN hash VARCHAR(64) NULL DEFAULT NULL;

ALTER TABLE PENDING_PAYMENT
	ADD INDEX address_pending_payment_status_id (address, pending_payment_status_id);

INSERT INTO TRANSACTION_SOURCE_TYPE (id, name) VALUES (8, 'BTC_INVOICE');

UPDATE MERCHANT SET transaction_source_type_id=8 WHERE  id=3;

ALTER TABLE TRANSACTION
	CHANGE COLUMN source_type source_type ENUM('ORDER','MERCHANT','REFERRAL','ACCRUAL','MANUAL','USER_TRANSFER','INVOICE','BTC_INVOICE') NULL DEFAULT NULL;
