/*------------ В САМОМ КОНЦЕ----------------------------------*/


SELECT TX.source_type, COUNT(*)
FROM TRANSACTION TX
GROUP BY TX.source_type
/*CHECK :
MERCHANT, BTC_INVOICE, INVOICE must not be presented*/

source_type | COUNT(*)
ORDER | 1348705
MERCHANT | 3   <<<<<<<<<<<<<<<<<<<< !!!
REFERRAL | 770286
MANUAL | 749
USER_TRANSFER | 286
WITHDRAW | 41506


ORDER	1552999
MERCHANT	3
REFERRAL	904650
MANUAL	827
USER_TRANSFER	340
WITHDRAW	43467



 */

/*BACKUP AND DELETE TRANSACTIONS that is bind with none source_type*/
INSERT INTO TRANSACTION_BACKUP_REFILL
SELECT TX.* FROM TRANSACTION TX
WHERE TX.source_type='MERCHANT';
/* Затронуто строк: 3*/

DELETE FROM TRANSACTION
WHERE source_type='MERCHANT';
/* Затронуто строк: 3*/

SELECT MAX(id) FROM TRANSACTION -> 2 862 577   remember id after which new tranactins will be inserted

SELECT status_id, count(*)
FROM REFILL_REQUEST
GROUP BY status_id
/*
"status_id"	"count(*)"
4 | 20816
6 | 1
9 | 14770 <<<<<<<<<<
10 | 1387 <<<<<<<<<<
11 | 346
12 | 5834
>>>>>>>>>>> 14 770 +1 387 = 16 157
new
3	2
4	21737
5	1
6	1
9	15331
10	1423
11	354
12	5890
>>>>>>>>>>> 15331 + 1423 = 16 754
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
/* Затронуто строк: 16 754*/


INSERT INTO REFILL_REQUEST_CONFIRMATION
(refill_request_id, datetime, confirmation_number, amount)
(SELECT id, status_modification_date, RR.confirmation, RR.amount
FROM REFILL_REQUEST RR
WHERE RR.confirmation>=0)
/* Затронуто строк: 4 747*/


ALTER TABLE REFILL_REQUEST
	DROP COLUMN confirmation;
	
ALTER TABLE TRANSACTION
	CHANGE COLUMN source_type source_type ENUM('ORDER','REFERRAL','ACCRUAL','MANUAL','USER_TRANSFER','WITHDRAW','REFILL', 'STOP_ORDER') NULL DEFAULT NULL;
/*------------ В САМОМ КОНЦЕ----------------------------------*/

