
/*FROM INVOICE_REQUEST*/

SELECT PP.invoice_request_status_id, TX.provided, TX.source_type, COUNT(*)
FROM INVOICE_REQUEST PP
JOIN TRANSACTION TX ON  TX.id=PP.transaction_id AND TX.operation_type_id=1
GROUP BY PP.invoice_request_status_id, TX.provided, TX.source_type;

/*
invoice_request_status_id | provided | source_type | COUNT(*)
3 | 0 | MERCHANT | 5
3 | 0 | INVOICE | 215
4 | 1 | MERCHANT | 20
4 | 1 | INVOICE | 1266
6 | 0 | MERCHANT | 1742
6 | 0 | INVOICE | 625

3 873

new
1	0	INVOICE	2
2	0	INVOICE	1
3	0	MERCHANT	5
3	0	INVOICE	218
4	1	MERCHANT	20
4	1	INVOICE	1301
6	0	MERCHANT	1742
6	0	INVOICE	642
3931
 */

SELECT MAX(id) FROM REFILL_REQUEST --> 57 146

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
CASE
	WHEN IR.invoice_request_status_id = 1 THEN 3 /*CREATED_USER - WAITING_CONFIRMATION_USER*/
	WHEN IR.invoice_request_status_id = 2 THEN 5 /*CONFIRMED_USER - CONFIRMED_USER*/
	WHEN IR.invoice_request_status_id = 3 THEN 11 /*REVOKED_USER - REVOKED_USER*/
	WHEN IR.invoice_request_status_id = 4 THEN 10 /*ACCEPTED_ADMIN - ACCEPTED_ADMIN*/
	WHEN IR.invoice_request_status_id = 5 THEN 8 /*DECLINED_ADMIN - DECLINED_ADMIN*/
	WHEN IR.invoice_request_status_id = 6 THEN 12 /*EXPIRED - EXPIRED*/
END,
TX.datetime, TX.datetime,
TX.currency_id, WALLET.user_id,
TX.commission_id, TX.merchant_id,
IR.bank_id, IR.user_full_name, IR.remark, IR.payer_bank_name, IR.payer_bank_code, IR.payer_account,
IR.receipt_scan, IR.receipt_scan_name,
NULL,
NULL,
NULL,
NULL,
CONCAT('FROM INVOICE_REQUEST FOR status', IR.invoice_request_status_id)
FROM TRANSACTION TX
JOIN INVOICE_REQUEST IR ON IR.transaction_id=TX.id /*with INVOICE_REQUEST related with TX source_type MERCHANT and INVOICE*/
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.operation_type_id=1
/* Затронуто строк: 3 931*/

UPDATE TRANSACTION TX
JOIN INVOICE_REQUEST IR ON IR.transaction_id=TX.id
JOIN WALLET ON WALLET.id = TX.user_wallet_id
SET TX.description = 'EXPORTED TO REFILL_REQUEST FROM INVOICE_REQUEST'
WHERE TX.operation_type_id=1;
/* Затронуто строк: 3 931*/

INSERT INTO TRANSACTION_BACKUP_REFILL
SELECT TX.* FROM TRANSACTION TX
JOIN INVOICE_REQUEST IR ON IR.transaction_id=TX.id /*with INVOICE_REQUEST related with TX source_type MERCHANT and INVOICE*/
JOIN WALLET ON WALLET.id = TX.user_wallet_id
WHERE TX.operation_type_id=1
/* Затронуто строк: 3 931*/
/*--------------------------------------------------------*/


/*------------ В КОНЦЕ----------------------------------*/

DROP TABLE IF EXISTS INVOICE_REQUEST_BACKUP;
CREATE TABLE INVOICE_REQUEST_BACKUP (
	transaction_id INT(11) NOT NULL,
	user_id INT(11) NOT NULL,
	remark VARCHAR(300) NULL DEFAULT NULL,
	user_full_name VARCHAR(250) NULL DEFAULT NULL,
	acceptance_user_id INT(11) NULL DEFAULT NULL,
	acceptance_time DATETIME NULL DEFAULT NULL,
	bank_id INT(11) NULL DEFAULT NULL,
	payer_bank_name VARCHAR(200) NULL DEFAULT NULL,
	payer_account VARCHAR(100) NULL DEFAULT NULL,
	receipt_scan VARCHAR(100) NULL DEFAULT NULL,
	payer_bank_code VARCHAR(10) NULL DEFAULT NULL,
	invoice_request_status_id INT(11) NOT NULL,
	status_update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	receipt_scan_name VARCHAR(50) NULL DEFAULT NULL,
	UNIQUE INDEX transaction_id_UNIQUE (transaction_id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB

INSERT INTO INVOICE_REQUEST_BACKUP
SELECT * FROM
INVOICE_REQUEST IR
WHERE EXISTS(SELECT *
FROM TRANSACTION TX
WHERE description = 'EXPORTED TO REFILL_REQUEST FROM INVOICE_REQUEST' AND TX.operation_type_id=1
AND TX.id=IR.transaction_id
);
/* Затронуто строк: 3 931*/

DELETE FROM
INVOICE_REQUEST
WHERE EXISTS(SELECT *
FROM TRANSACTION TX
WHERE description = 'EXPORTED TO REFILL_REQUEST FROM INVOICE_REQUEST' AND TX.operation_type_id=1
AND TX.id=INVOICE_REQUEST.transaction_id
)
/* Затронуто строк: 3 931*/

DELETE FROM TRANSACTION
WHERE description = 'EXPORTED TO REFILL_REQUEST FROM INVOICE_REQUEST'
/* Затронуто строк: 3 931*/

/*------------ В КОНЦЕ----------------------------------*/



