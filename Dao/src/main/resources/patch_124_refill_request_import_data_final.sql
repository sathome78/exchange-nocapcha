/*------------ В САМОМ КОНЦЕ----------------------------------*/


SELECT TX.source_type, COUNT(*)
FROM TRANSACTION TX
GROUP BY TX.source_type
/*CHECK :
MERCHANT, BTC_INVOICE, INVOICE must not be presented*/

"source_type"	"COUNT(*)"
"ORDER"	"1117930"
"MERCHANT"	"3" <<<<<<<<<<<<<<<<<<<<<< !!!!
"REFERRAL"	"630452"
"MANUAL"	"737"
"USER_TRANSFER"	"238"
"WITHDRAW"	"34684"

 */

/*BACKUP AND DELETE TRANSACTIONS that is bind with none source_type*/
INSERT INTO TRANSACTION_BACKUP_REFILL
SELECT TX.* FROM TRANSACTION TX
WHERE TX.source_type='MERCHANT';
/* Затронуто строк: 3*/

DELETE FROM TRANSACTION
WHERE source_type='MERCHANT';
/* Затронуто строк: 3*/

SELECT MAX(id) FROM TRANSACTION -> 1914669   remember id after which new tranactins will be inserted

SELECT status_id, count(*)
FROM REFILL_REQUEST
GROUP BY status_id
/*
"status_id"	"count(*)"
"3"	"7"
"4"	"18561"
"8"	"1"
"9"	"12973"  <<<<<<<<<<<<<<<<
"10"	"1237" <<<<<<<<<<<<<<<<
"11"	"313"
"12"	"5664"
>>>>>>>>>>> 12 973+1 237 = 14 210
 */

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
WHERE RR.status_id IN (9 /*ACCEPTED_AUTO*/, 10 /*ACCEPTED_ADMIN*/)
);
/* Затронуто строк: 14 210*/


INSERT INTO REFILL_REQUEST_CONFIRMATION
(refill_request_id, datetime, confirmation_number)
(SELECT id, status_modification_date, RR.confirmation
FROM REFILL_REQUEST RR
WHERE RR.confirmation>=0)
/* Затронуто строк: 4 581*/


ALTER TABLE REFILL_REQUEST
	DROP COLUMN confirmation;
	
ALTER TABLE TRANSACTION
	CHANGE COLUMN source_type source_type ENUM('ORDER','REFERRAL','ACCRUAL','MANUAL','USER_TRANSFER','WITHDRAW','REFILL') NULL DEFAULT NULL;
/*------------ В САМОМ КОНЦЕ----------------------------------*/

