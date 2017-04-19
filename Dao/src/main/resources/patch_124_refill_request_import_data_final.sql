/*------------ В САМОМ КОНЦЕ----------------------------------*/


SELECT TX.source_type, COUNT(*)
FROM TRANSACTION TX
GROUP BY TX.source_type
/*CHECK :
MERCHANT, BTC_INVOICE, INVOICE must not be presented*/
 */

SELECT MAX(id) FROM TRANSACTION -> 1223124   remember id after which new tranactins will be inserted

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


INSERT INTO REFILL_REQUEST_CONFIRMATION
(refill_request_id, datetime, confirmation_number)
(SELECT id, status_modification_date, RR.confirmation
FROM REFILL_REQUEST RR
WHERE RR.confirmation>=0)



ALTER TABLE REFILL_REQUEST
	DROP COLUMN confirmation;
/*------------ В САМОМ КОНЦЕ----------------------------------*/