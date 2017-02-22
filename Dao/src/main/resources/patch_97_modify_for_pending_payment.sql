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

ALTER TABLE PENDING_PAYMENT ADD COLUMN `pending_payment_status_id` INT NULL;

ALTER TABLE TRANSACTION
	ADD INDEX id_source_type (id, source_type);

UPDATE PENDING_PAYMENT PP
SET pending_payment_status_id = 3
WHERE EXISTS (
SELECT * FROM TRANSACTION TX
WHERE TX.id = PP.invoice_id
AND TX.company_wallet_id=4
AND TX.provided = 1
AND TX.confirmation>0
);

UPDATE PENDING_PAYMENT PP
SET pending_payment_status_id = 4
WHERE EXISTS (
SELECT * FROM TRANSACTION TX
WHERE TX.id = PP.invoice_id
AND TX.company_wallet_id=4
AND TX.provided = 1
AND TX.confirmation<=0
);


UPDATE PENDING_PAYMENT PP
SET pending_payment_status_id = 6
WHERE EXISTS (
SELECT * FROM TRANSACTION TX
WHERE TX.id = PP.invoice_id
AND TX.company_wallet_id=4
AND TX.provided = 0
AND TX.confirmation>0
);

UPDATE PENDING_PAYMENT PP
SET pending_payment_status_id = 1
WHERE EXISTS (
SELECT * FROM TRANSACTION TX
WHERE TX.id = PP.invoice_id
AND TX.company_wallet_id=4
AND TX.provided = 0
AND TX.confirmation<=0
);

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

UPDATE PENDING_PAYMENT PP
SET
hash = (SELECT hash FROM BTC_TRANSACTION BTX WHERE BTX.transaction_id = PP.invoice_id),
acceptance_user_id = (SELECT acceptance_user_id FROM BTC_TRANSACTION BTX WHERE BTX.transaction_id = PP.invoice_id),
acceptance_time = (SELECT acceptance_time FROM BTC_TRANSACTION BTX WHERE BTX.transaction_id = PP.invoice_id)
WHERE EXISTS (
SELECT transaction_id FROM BTC_TRANSACTION BTX WHERE BTX.transaction_id = PP.invoice_id
);

-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

MAKE IT BEFORE NEXT UPDATE

SELECT source_type, COUNT(*)
FROM TRANSACTION TX
WHERE TX.company_wallet_id=4
GROUP BY source_type
--!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

UPDATE TRANSACTION TX
SET source_type = 8,
source_id = id
WHERE TX.company_wallet_id=4
AND source_type = 2
