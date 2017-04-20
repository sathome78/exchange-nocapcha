DROP TABLE IF EXISTS TRANSACTION_BACKUP_REFILL;
CREATE TABLE TRANSACTION_BACKUP_REFILL (
	id INT(40) NOT NULL AUTO_INCREMENT,
	user_wallet_id INT(11) NOT NULL,
	company_wallet_id INT(11) NULL DEFAULT NULL,
	amount DOUBLE(40,9) NOT NULL,
	commission_amount DOUBLE(40,9) NOT NULL,
	commission_id INT(11) NULL DEFAULT NULL,
	operation_type_id INT(11) NOT NULL,
	currency_id INT(11) NOT NULL,
	merchant_id INT(11) NULL DEFAULT NULL,
	datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	provided TINYINT(1) NOT NULL DEFAULT '0',
	confirmation INT(11) NULL DEFAULT '-1',
	order_id INT(11) NULL DEFAULT NULL,
	status_id INT(11) NOT NULL DEFAULT '1',
	status_modification_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	active_balance_before DOUBLE(40,9) NULL DEFAULT NULL,
	reserved_balance_before DOUBLE(40,9) NULL DEFAULT NULL,
	company_balance_before DOUBLE(40,9) NULL DEFAULT NULL,
	company_commission_balance_before DOUBLE(40,9) NULL DEFAULT NULL,
	source_type ENUM('ORDER','MERCHANT','REFERRAL','ACCRUAL','MANUAL','USER_TRANSFER','INVOICE','BTC_INVOICE','WITHDRAW') NULL DEFAULT NULL,
	source_id INT(40) NULL DEFAULT NULL,
	provided_modification_date TIMESTAMP NULL DEFAULT NULL,
	description VARCHAR(100) NULL DEFAULT NULL,
	PRIMARY KEY (id)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

/*temporary field*/
ALTER TABLE REFILL_REQUEST
	ADD COLUMN confirmation INT(11) NULL DEFAULT NULL;

/*FROM PENDING_PAYMENT*/


DELETE FROM REFILL_REQUEST;

/*currencies excepted BTC (excluded by 'source_type='MERCHANT'') and EDC (excluded by ' NOT EXISTS(...)') and INVOICE (excluded by NOT EXISTS(SELECT * FROM INVOICE_REQUEST...) )*/
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
confirmation)
SELECT
IF(BTX.amount IS NOT NULL, BTX.amount, TX.amount), TX.commission_amount, /*TX.commission_amount corresponds to BTX.amount (if BTX.amount is present and not equals to TX.amount)*/
IF(TX.provided=0, 4/*ON_PENDING*/, 9/*ACCEPTED_AUTO*/), TX.datetime, IF(BTX.acceptance_time IS NOT NULL, BTX.acceptance_time, IF(PP.status_update_date IS NOT NULL, PP.status_update_date, TX.datetime)),
TX.currency_id, WALLET.user_id,
TX.commission_id, TX.merchant_id,
NULL, NULL, NULL, NULL, NULL, NULL,
NULL, NULL,
PP.address,
PP.transaction_hash,
IF(BTX.hash IS NOT NULL, BTX.hash, PP.hash),
BTX.acceptance_user_id,
CONCAT('FROM PENDING_PAYMENT (MERCHANT) FOR provided',TX.provided),
TX.confirmation
FROM TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
LEFT JOIN BTC_TRANSACTION BTX ON BTX.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='MERCHANT' /*EXCLUDES BTC*/ AND TX.operation_type_id=1
AND NOT EXISTS(SELECT * FROM EDC_MERCHANT_TRANSACTION EMT WHERE EMT.transaction_id=TX.id)  /*EXCLUDES EDC Merchant variant*/
AND NOT EXISTS(SELECT * FROM EDC_TEMP_ACCOUNT ETA WHERE ETA.transaction_id=TX.id) /*EXCLUDES  EDC Node variant*/
AND NOT EXISTS(SELECT * FROM INVOICE_REQUEST IR WHERE IR.transaction_id=TX.id) /*EXCLUDES INVOICE that has type MERCHANT*/
/* Затронуто строк: 9 673 */

SELECT TX.provided, COUNT(*)
FROM TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
LEFT JOIN BTC_TRANSACTION BTX ON BTX.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='MERCHANT' AND TX.operation_type_id=1
AND NOT EXISTS(SELECT * FROM EDC_MERCHANT_TRANSACTION EMT WHERE EMT.transaction_id=TX.id)
AND NOT EXISTS(SELECT * FROM EDC_TEMP_ACCOUNT ETA WHERE ETA.transaction_id=TX.id)
AND NOT EXISTS(SELECT * FROM INVOICE_REQUEST IR WHERE IR.transaction_id=TX.id)
GROUP BY TX.provided;
/*
"provided"	"COUNT(*)"
"0"	"6692"
"1"	"2981"
    9 673
*/

UPDATE TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
SET TX.description = 'EXPORTED TO REFILL_REQUEST'
WHERE TX.source_type='MERCHANT' AND TX.operation_type_id=1
AND NOT EXISTS(SELECT * FROM EDC_MERCHANT_TRANSACTION EMT WHERE EMT.transaction_id=TX.id)
AND NOT EXISTS(SELECT * FROM EDC_TEMP_ACCOUNT ETA WHERE ETA.transaction_id=TX.id)
AND NOT EXISTS(SELECT * FROM INVOICE_REQUEST IR WHERE IR.transaction_id=TX.id);
/* Затронуто строк: 9 673 */

/*--------------------------------------------------------*/

/*BTC*/
SELECT TX.currency_id, TX.provided, IF(PP.invoice_id IS NULL, 0, 1) as isPP, PP.pending_payment_status_id, COUNT(*)
FROM TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
WHERE TX.source_type='BTC_INVOICE' AND TX.operation_type_id=1
GROUP BY TX.currency_id, TX.provided, isPP, PP.pending_payment_status_id;

/*
"currency_id"	"provided"	"isPP"	"pending_payment_status_id"	"COUNT(*)"
"4"	"0"	"1"	"2"	"116"
"4"	"0"	"1"	"5"	"3349"
"4"	"1"	"1"	"3"	"1018"
"4"	"1"	"1"	"4"	"95"
4578
*/

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
import_note,
confirmation)
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
CONCAT('FROM PENDING_PAYMENT (BTC_INVOICE) FOR status', PP.pending_payment_status_id),
TX.confirmation
FROM TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON PP.invoice_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='BTC_INVOICE' AND TX.operation_type_id=1;
/* Затронуто строк: 4 578*/

UPDATE TRANSACTION TX
LEFT JOIN PENDING_PAYMENT PP ON  PP.invoice_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
SET TX.description = 'EXPORTED TO REFILL_REQUEST'
WHERE TX.source_type='BTC_INVOICE' AND TX.operation_type_id=1;
/* Затронуто строк: 4 578 */

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
import_note,
confirmation)
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
CONCAT('FROM EDC_MERCHANT_TRANSACTION', ''),
TX.confirmation
FROM TRANSACTION TX
JOIN EDC_MERCHANT_TRANSACTION EMT ON EMT.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='MERCHANT' AND TX.operation_type_id=1;
/* Затронуто строк: 5 655*/

SELECT COUNT(*)
FROM TRANSACTION TX
JOIN EDC_MERCHANT_TRANSACTION EMT ON EMT.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.source_type='MERCHANT' AND TX.operation_type_id=1;
/*5 655*/

UPDATE TRANSACTION TX
JOIN EDC_MERCHANT_TRANSACTION EMT ON EMT.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
SET TX.description = 'EXPORTED TO REFILL_REQUEST'
WHERE TX.source_type='MERCHANT' AND TX.operation_type_id=1;
/* Затронуто строк: 5 655*/

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
import_note,
confirmation)
SELECT
0, 0,
4/*ON_PENDING*/, NOW(), NOW(),
CUR.id, USER.id,
NULL, 13, /*E-DinarCoin*/
NULL, NULL, NULL, NULL, NULL, NULL,
NULL, NULL,
EMA.address,
NULL,
NULL,
NULL,
CONCAT('FROM EDC_MERCHANT_ACCOUNT', ''),
NULL
FROM EDC_MERCHANT_ACCOUNT EMA
JOIN USER ON USER.id=EMA.user_id
JOIN CURRENCY CUR ON (CUR.name='EDR');
/* Затронуто строк: 7 190*/

SELECT COUNT(*)
FROM EDC_MERCHANT_ACCOUNT EMA
JOIN USER ON USER.id=EMA.user_id
JOIN CURRENCY CUR ON (CUR.name='EDR');
/*7 190*/

/*transactions are absent*/     /*this count not be in TRANSACTION_BACKUP_REFILL*/
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
wif_priv_key, pub_key, brain_priv_key,
confirmation)
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
ETA.wif_priv_key, ETA.pub_key, ETA.brain_priv_key,
TX.confirmation
FROM TRANSACTION TX
JOIN EDC_TEMP_ACCOUNT ETA ON ETA.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
LEFT JOIN PENDING_PAYMENT PP ON (PP.invoice_id=TX.id)
WHERE TX.source_type='MERCHANT' AND TX.operation_type_id=1
AND NOT (TX.provided=0 AND PP.invoice_id IS NULL); /*it's failed records: not provided but without pending_payment - should be ignored*/;
/* Затронуто строк: 7 998*/

SELECT DISTINCT TX.provided, IF(PP.invoice_id IS NULL, 0, 1) as isPP, COUNT(*)
FROM TRANSACTION TX
JOIN EDC_TEMP_ACCOUNT ETA ON ETA.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
LEFT JOIN PENDING_PAYMENT PP ON (PP.invoice_id=TX.id)
WHERE TX.source_type='MERCHANT' AND TX.operation_type_id=1
AND NOT (TX.provided=0 AND PP.invoice_id IS NULL) /*it's failed records: not provided but without pending_payment - should be ignored*/
GROUP BY TX.provided, isPP;
/*
"provided"	"isPP"	"COUNT(*)"
"0"	"1"	"4679"
"1"	"0"	"3319"
7 998
*/

SELECT COUNT(*)  /*count the failed records*/
FROM TRANSACTION TX
JOIN EDC_TEMP_ACCOUNT ETA ON ETA.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
LEFT JOIN PENDING_PAYMENT PP ON (PP.invoice_id=TX.id)
WHERE TX.source_type='MERCHANT' AND TX.operation_type_id=1
AND (TX.provided=0 AND PP.invoice_id IS NULL)
/*64*/

UPDATE TRANSACTION TX
JOIN EDC_TEMP_ACCOUNT ETA ON ETA.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
LEFT JOIN PENDING_PAYMENT PP ON (PP.invoice_id=TX.id)
SET TX.description = 'EXPORTED TO REFILL_REQUEST'
WHERE TX.source_type='MERCHANT' AND TX.operation_type_id=1; /*WHERE NOT (...) - not needed - update, backup and then remove including failed*/
/* Затронуто строк: 8 062*/

/*--------------------------------------------------------*/


/*------------ В КОНЦЕ----------------------------------*/

INSERT INTO TRANSACTION_BACKUP_REFILL
SELECT * FROM TRANSACTION TX
WHERE description = 'EXPORTED TO REFILL_REQUEST';
/* Затронуто строк: 27 968 = summa of all counts*/

DROP TABLE PENDING_BLOCKCHAIN_PAYMENT;

DROP TABLE IF EXISTS PENDING_PAYMENT_BACKUP;
CREATE TABLE PENDING_PAYMENT_BACKUP (
	invoice_id INT(11) NOT NULL,
	transaction_hash VARCHAR(64) NOT NULL,
	address VARCHAR(64) NULL DEFAULT NULL,
	pending_payment_status_id INT(11) NULL DEFAULT NULL,
	status_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	acceptance_user_id INT(11) NULL DEFAULT NULL,
	acceptance_time DATETIME NULL DEFAULT NULL,
	hash VARCHAR(64) NULL DEFAULT NULL,
	PRIMARY KEY (invoice_id)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

INSERT INTO PENDING_PAYMENT_BACKUP
SELECT * FROM
PENDING_PAYMENT PP
WHERE EXISTS(SELECT *
FROM TRANSACTION TX
WHERE description = 'EXPORTED TO REFILL_REQUEST' AND TX.operation_type_id=1
AND TX.source_type ='BTC_INVOICE' AND TX.id=PP.invoice_id
);
/* Затронуто строк: 4 578*/

DELETE FROM
PENDING_PAYMENT
WHERE EXISTS(SELECT *
FROM TRANSACTION TX
WHERE description = 'EXPORTED TO REFILL_REQUEST' AND TX.operation_type_id=1
AND TX.source_type = 'BTC_INVOICE' AND TX.id=PENDING_PAYMENT.invoice_id
);
/* Затронуто строк: 4 578*/

INSERT INTO PENDING_PAYMENT_BACKUP
SELECT * FROM
PENDING_PAYMENT PP
WHERE EXISTS(SELECT *
FROM TRANSACTION TX
WHERE description = 'EXPORTED TO REFILL_REQUEST' AND TX.operation_type_id=1
AND TX.source_type ='MERCHANT' AND TX.id=PP.invoice_id
);
/* Затронуто строк: 6 912*/

DELETE FROM
PENDING_PAYMENT
WHERE EXISTS(SELECT *
FROM TRANSACTION TX
WHERE description = 'EXPORTED TO REFILL_REQUEST' AND TX.operation_type_id=1
AND TX.source_type = 'MERCHANT' AND TX.id=PENDING_PAYMENT.invoice_id
)
/* Затронуто строк: 6 912*/


DROP TABLE IF EXISTS BTC_TRANSACTION_BACKUP;
CREATE TABLE BTC_TRANSACTION_BACKUP (
	hash VARCHAR(64) NOT NULL,
	amount DOUBLE(40,9) NOT NULL,
	transaction_id INT(11) NULL DEFAULT NULL,
	acceptance_user_id INT(11) NULL DEFAULT NULL,
	acceptance_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (hash)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

INSERT INTO BTC_TRANSACTION_BACKUP
SELECT * FROM BTC_TRANSACTION;

DELETE FROM BTC_TRANSACTION;

DROP TABLE IF EXISTS EDC_TEMP_ACCOUNT_BACKUP;
CREATE TABLE EDC_TEMP_ACCOUNT_BACKUP (
	transaction_id INT(11) NOT NULL,
	wif_priv_key VARCHAR(256) NOT NULL,
	pub_key VARCHAR(256) NOT NULL,
	brain_priv_key VARCHAR(256) NOT NULL,
	account_id VARCHAR(256) NULL DEFAULT NULL,
	account_name VARCHAR(256) NOT NULL,
	used TINYINT(1) NOT NULL DEFAULT '0',
	PRIMARY KEY (transaction_id)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

INSERT INTO EDC_TEMP_ACCOUNT_BACKUP
SELECT * FROM EDC_TEMP_ACCOUNT;

DELETE FROM EDC_TEMP_ACCOUNT;

DROP TABLE IF EXISTS EDC_MERCHANT_TRANSACTION_BACKUP;
CREATE TABLE EDC_MERCHANT_TRANSACTION_BACKUP (
	merchant_transaction_id VARCHAR(256) NOT NULL,
	transaction_id INT(40) NOT NULL,
	address VARCHAR(256) NOT NULL,
	PRIMARY KEY (merchant_transaction_id, transaction_id)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;

INSERT INTO EDC_MERCHANT_TRANSACTION_BACKUP
SELECT * FROM EDC_MERCHANT_TRANSACTION;

DELETE FROM EDC_MERCHANT_TRANSACTION;

/**/

DELETE FROM TRANSACTION
WHERE description = 'EXPORTED TO REFILL_REQUEST'
/* Затронуто строк: 27 968*/


/*------------ В КОНЦЕ----------------------------------*/



