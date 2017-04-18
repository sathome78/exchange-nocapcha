CREATE TABLE `TRANSACTION_BACKUP_REFILL` (
	`id` INT(40) NOT NULL AUTO_INCREMENT,
	`user_wallet_id` INT(11) NOT NULL,
	`company_wallet_id` INT(11) NULL DEFAULT NULL,
	`amount` DOUBLE(40,9) NOT NULL,
	`commission_amount` DOUBLE(40,9) NOT NULL,
	`commission_id` INT(11) NULL DEFAULT NULL,
	`operation_type_id` INT(11) NOT NULL,
	`currency_id` INT(11) NOT NULL,
	`merchant_id` INT(11) NULL DEFAULT NULL,
	`datetime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`provided` TINYINT(1) NOT NULL DEFAULT '0',
	`confirmation` INT(11) NULL DEFAULT '-1',
	`order_id` INT(11) NULL DEFAULT NULL,
	`status_id` INT(11) NOT NULL DEFAULT '1',
	`status_modification_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`active_balance_before` DOUBLE(40,9) NULL DEFAULT NULL,
	`reserved_balance_before` DOUBLE(40,9) NULL DEFAULT NULL,
	`company_balance_before` DOUBLE(40,9) NULL DEFAULT NULL,
	`company_commission_balance_before` DOUBLE(40,9) NULL DEFAULT NULL,
	`source_type` ENUM('ORDER','MERCHANT','REFERRAL','ACCRUAL','MANUAL','USER_TRANSFER','INVOICE','BTC_INVOICE','WITHDRAW') NULL DEFAULT NULL,
	`source_id` INT(40) NULL DEFAULT NULL,
	`provided_modification_date` TIMESTAMP NULL DEFAULT NULL,
	`description` VARCHAR(100) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;



/*FROM PENDING_PAYMENT*/

SELECT COUNT(*)
FROM TRANSACTION TX
WHERE TX.source_type='MERCHANT';

/*currencies excepted BTC (excluded by 'source_type='MERCHANT'') and EDC (excluded by ' NOT EXISTS(...)')*/
INSERT INTO REFILL_REQUEST
(amount, commission,
status_id, date_creation, status_modification_date,
currency_id, user_id,
commission_id, merchant_id,
recipient_bank_id, user_full_name, remark, payer_bank_name, payer_bank_code, payer_account,
receipt_scan, receipt_scan_name,
address,
merchant_transaction_id,
hash,
admin_holder_id,
import_note)
SELECT
TX.amount, TX.commission_amount,
IF(TX.provided=0, 4/*ON_PENDING*/, 9/*ACCEPTED_AUTO*/), TX.datetime, TX.datetime,
TX.currency_id, WALLET.user_id,
TX.commission_id, TX.merchant_id,
NULL, NULL, NULL, NULL, NULL, NULL,
NULL, NULL,
PP.address,
PP.transaction_hash,
PP.hash,
NULL,
CONCAT('FROM PENDING_PAYMENT FOR provided',TX.provided)
FROM TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='MERCHANT' /*EXCLUDES BTC*/
AND NOT EXISTS(SELECT * FROM EDC_MERCHANT_TRANSACTION EMT WHERE EMT.transaction_id=TX.id)  /*EXCLUDES EDC Merchant variant*/
AND NOT EXISTS(SELECT * FROM EDC_TEMP_ACCOUNT ETA WHERE ETA.transaction_id=TX.id) /*EXCLUDES  EDC Node variant*/;

SELECT TX.provided, COUNT(*)
FROM TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='MERCHANT'
AND NOT EXISTS(SELECT * FROM EDC_MERCHANT_TRANSACTION EMT WHERE EMT.transaction_id=TX.id)
AND NOT EXISTS(SELECT * FROM EDC_TEMP_ACCOUNT ETA WHERE ETA.transaction_id=TX.id)
GROUP BY TX.provided;

UPDATE TRANSACTION TX
SET TX.description = 'EXPORTED TO REFILL_REQUEST'
WHERE TX.source_type='MERCHANT'
AND NOT EXISTS(SELECT * FROM EDC_MERCHANT_TRANSACTION EMT WHERE EMT.transaction_id=TX.id)
AND NOT EXISTS(SELECT * FROM EDC_TEMP_ACCOUNT ETA WHERE ETA.transaction_id=TX.id);

INSERT INTO TRANSACTION_BACKUP_REFILL
SELECT * FROM TRANSACTION TX
WHERE TX.source_type='MERCHANT'
AND NOT EXISTS(SELECT * FROM EDC_MERCHANT_TRANSACTION EMT WHERE EMT.transaction_id=TX.id)
AND NOT EXISTS(SELECT * FROM EDC_TEMP_ACCOUNT ETA WHERE ETA.transaction_id=TX.id);

/*--------------------------------------------------------*/

/*BTC*/
SELECT TX.currency_id, TX.provided, IF(PP.invoice_id IS NULL, 0, 1) as isPP, PP.pending_payment_status_id, COUNT(*)
FROM TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
WHERE TX.source_type='BTC_INVOICE'
GROUP BY TX.currency_id, TX.provided, isPP, PP.pending_payment_status_id;

INSERT INTO REFILL_REQUEST
(amount, commission,
status_id,
date_creation, status_modification_date,
currency_id, user_id,
commission_id, merchant_id,
recipient_bank_id, user_full_name, remark, payer_bank_name, payer_bank_code, payer_account,
receipt_scan, receipt_scan_name,
address,
merchant_transaction_id,
hash,
admin_holder_id,
import_note)
SELECT
TX.amount, TX.commission_amount,
CASE
	WHEN PP.pending_payment_status_id = 1 THEN 4
	WHEN PP.pending_payment_status_id = 2 THEN 11
	WHEN PP.pending_payment_status_id = 3 THEN 9
	WHEN PP.pending_payment_status_id = 4 THEN 10
	WHEN PP.pending_payment_status_id = 5 THEN 12
	WHEN PP.pending_payment_status_id = 6 THEN 6
END,
TX.datetime, TX.datetime,
TX.currency_id, WALLET.user_id,
TX.commission_id, TX.merchant_id,
NULL, NULL, NULL, NULL, NULL, NULL,
NULL, NULL,
PP.address,
PP.transaction_hash,
PP.hash,
NULL,
CONCAT('FROM PENDING_PAYMENT FOR status', PP.pending_payment_status_id)
FROM TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='BTC_INVOICE';

UPDATE TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
SET TX.description = 'EXPORTED TO REFILL_REQUEST'
WHERE TX.source_type='BTC_INVOICE';

INSERT INTO TRANSACTION_BACKUP_REFILL
SELECT * FROM TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='BTC_INVOICE';
/*--------------------------------------------------------*/

/*FROM EDC_MERCHANT_TRANSACTION*/

/*EDC Merchant provided (EDR-Merchant variant)*/
INSERT INTO REFILL_REQUEST
(amount, commission,
status_id, date_creation, status_modification_date,
currency_id, user_id,
commission_id, merchant_id,
recipient_bank_id, user_full_name, remark, payer_bank_name, payer_bank_code, payer_account,
receipt_scan, receipt_scan_name,
address,
merchant_transaction_id,
hash,
admin_holder_id,
import_note)
SELECT
TX.amount, TX.commission_amount,
9/*ACCEPTED_AUTO*/, TX.datetime, TX.datetime,
TX.currency_id, WALLET.user_id,
TX.commission_id, TX.merchant_id,
NULL, NULL, NULL, NULL, NULL, NULL,
NULL, NULL,
EMT.address,
EMT.merchant_transaction_id,
NULL,
NULL,
CONCAT('FROM EDC_MERCHANT_TRANSACTION', '')
FROM TRANSACTION TX
JOIN EDC_MERCHANT_TRANSACTION EMT ON EMT.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='MERCHANT';

SELECT COUNT(*)
FROM TRANSACTION TX
JOIN EDC_MERCHANT_TRANSACTION EMT ON EMT.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='MERCHANT';

UPDATE TRANSACTION TX
JOIN EDC_MERCHANT_TRANSACTION EMT ON EMT.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
SET TX.description = 'EXPORTED TO REFILL_REQUEST'
WHERE TX.source_type='MERCHANT';

INSERT INTO TRANSACTION_BACKUP_REFILL
SELECT * FROM TRANSACTION TX
JOIN EDC_MERCHANT_TRANSACTION EMT ON EMT.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='MERCHANT';
/*--------------------------------------------------------*/

/*ADD TO REFILL_REQUEST records that absent in TRANSACTION*/

/*EDC Merchant NOT provided  (EDR-Merchant variant)*/
INSERT INTO REFILL_REQUEST
(amount, commission,
status_id, date_creation, status_modification_date,
currency_id, user_id,
commission_id, merchant_id,
recipient_bank_id, user_full_name, remark, payer_bank_name, payer_bank_code, payer_account,
receipt_scan, receipt_scan_name,
address,
merchant_transaction_id,
hash,
admin_holder_id,
import_note)
SELECT
0, 0,
4/*ON_PENDING*/, NULL, NULL,
CUR.id, USER.id,
NULL, 13, /*E-DinarCoin*/
NULL, NULL, NULL, NULL, NULL, NULL,
NULL, NULL,
EMA.address,
NULL,
NULL,
NULL,
CONCAT('FROM EDC_MERCHANT_ACCOUNT', '')
FROM EDC_MERCHANT_ACCOUNT EMA
JOIN USER ON USER.id=EMA.user_id
JOIN CURRENCY CUR ON (CUR.name='EDR');

SELECT COUNT(*)
FROM EDC_MERCHANT_ACCOUNT EMA
JOIN USER ON USER.id=EMA.user_id
JOIN CURRENCY CUR ON (CUR.name='EDR');

/*transactions are absent*/
/*--------------------------------------------------------*/

/*EDC Merchant (EDR- NODE variant)*/

INSERT INTO REFILL_REQUEST
(amount, commission,
status_id, date_creation, status_modification_date,
currency_id, user_id,
commission_id, merchant_id,
recipient_bank_id, user_full_name, remark, payer_bank_name, payer_bank_code, payer_account,
receipt_scan, receipt_scan_name,
address,
merchant_transaction_id,
hash,
admin_holder_id,
import_note,
wif_priv_key, pub_key, brain_priv_key)
SELECT
TX.amount, TX.commission_amount,
IF(TX.provided=0, 4/*ON_PENDING*/, 9/*ACCEPTED_AUTO*/), TX.datetime, TX.datetime,
TX.currency_id, WALLET.user_id,
TX.commission_id, TX.merchant_id,
NULL, NULL, NULL, NULL, NULL, NULL,
NULL, NULL,
PP.address,
PP.transaction_hash,
NULL,
NULL,
CONCAT('FROM EDC_TEMP_ACCOUNT', ''),
ETA.wif_priv_key, ETA.pub_key, ETA.brain_priv_key
FROM TRANSACTION TX
JOIN EDC_TEMP_ACCOUNT ETA ON ETA.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
LEFT JOIN PENDING_PAYMENT PP ON (PP.invoice_id=TX.id)
WHERE TX.source_type='MERCHANT'
AND NOT (TX.provided=0 AND PP.invoice_id IS NULL); /*it's failed records: not provided but without pending_payment - should be ignored*/;

SELECT DISTINCT TX.provided, IF(PP.invoice_id IS NULL, 0, 1) as isPP, COUNT(*)
FROM TRANSACTION TX
JOIN EDC_TEMP_ACCOUNT ETA ON ETA.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
LEFT JOIN PENDING_PAYMENT PP ON (PP.invoice_id=TX.id)
WHERE NOT (TX.provided=0 AND PP.invoice_id IS NULL) /*it's failed records: not provided but without pending_payment - should be ignored*/
GROUP BY TX.provided, isPP;

UPDATE TRANSACTION TX
JOIN EDC_TEMP_ACCOUNT ETA ON ETA.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
LEFT JOIN PENDING_PAYMENT PP ON (PP.invoice_id=TX.id)
SET TX.description = 'EXPORTED TO REFILL_REQUEST'
WHERE TX.source_type='MERCHANT'; /*WHERE NOT (...) - not needed - update, backup and then remove including failed*/

INSERT INTO TRANSACTION_BACKUP_REFILL
SELECT * FROM TRANSACTION TX
JOIN EDC_TEMP_ACCOUNT ETA ON ETA.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
LEFT JOIN PENDING_PAYMENT PP ON (PP.invoice_id=TX.id)
WHERE TX.source_type='MERCHANT';

/*--------------------------------------------------------*/


/*------------ В САМОМ КОНЦЕ----------------------------------*/

SELECT MAX(id) FROM TRANSACTION -> 1223124

DELETE FROM TRANSACTION
WHERE description = 'EXPORTED TO REFILL_REQUEST'

SELECT status_id, count(*)
FROM REFILL_REQUEST
GROUP BY status_id

INSERT INTO TRANSACTION
(user_wallet_id, company_wallet_id, amount, commission_amount, commission_id, operation_type_id, currency_id, merchant_id,
  datetime, provided, confirmation, order_id, status_id, status_modification_date,
  active_balance_before, reserved_balance_before, company_balance_before, company_commission_balance_before,
  source_type, source_id, provided_modification_date, description)
(SELECT W.id, CW.id, RR.amount, 0, COM.id, 1, RR.currency_id, RR.merchant_id,
  RR.date_creation, 1, -1, NULL, 1, NULL,
  NULL, NULL, NULL, NULL,
  'REFILL', RR.id, NULL, 'AUTO ADDED FOR REFILL post'
FROM REFILL_REQUEST RR
JOIN WALLET W ON (W.user_id=RR.user_id AND W.currency_id=RR.currency_id)
JOIN USER ON USER.id = W.user_id
JOIN COMPANY_WALLET CW ON (CW.currency_id=RR.currency_id)
JOIN COMMISSION COM ON (COM.operation_type = 1 AND user_role = USER.roleid)
WHERE RR.status IN (9 /*ACCEPTED_AUTO*/, 10 /*ACCEPTED_ADMIN*/)
);
/*------------ В САМОМ КОНЦЕ----------------------------------*/



