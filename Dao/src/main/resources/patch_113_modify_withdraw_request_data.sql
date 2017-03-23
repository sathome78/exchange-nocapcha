/*NEW -> WAITING_MANUAL_POSTING*/
UPDATE WITHDRAW_REQUEST
SET status_id = 2
WHERE status=1; 

/*ACCEPTED -> POSTED_MANUAL*/
UPDATE WITHDRAW_REQUEST
SET status_id = 9
WHERE status=2; 

/*DECLINED -> DECLINED_ADMIN*/
UPDATE WITHDRAW_REQUEST
SET status_id = 8
WHERE status=3; 

/*set values of new fields for old records*/
UPDATE WITHDRAW_REQUEST WR
JOIN TRANSACTION TX ON (TX.id=WR.transaction_id)
JOIN WALLET W ON W.id=TX.user_wallet_id
JOIN USER ON USER.id=W.user_id
SET WR.date_creation = TX.datetime,
WR.amount=TX.amount+TX.commission_amount,
WR.commission=TX.commission_amount,
WR.status_modification_date = IF(TX.provided_modification_date IS NOT NULL, TX.provided_modification_date, TX.status_modification_date),
WR.currency_id=TX.currency_id,
WR.user_id=USER.id,
WR.merchant_id=TX.merchant_id,
WR.commission_id=TX.commission_id,
WR.status_modification_date=WR.acceptance;

/*delete transactions binded with withdraw_payment creation*/
ALTER TABLE WITHDRAW_REQUEST
	DROP FOREIGN KEY WITHDRAW_REQUEST_ibfk_1;

DELETE FROM
TRANSACTION
WHERE EXISTS (
SELECT *
FROM WITHDRAW_REQUEST WR
WHERE WR.transaction_id = TRANSACTION.id);


/*create transactions corresponding to reservation money for withdraw_payment creation*/
INSERT INTO TRANSACTION
(user_wallet_id, company_wallet_id, amount, commission_amount, commission_id, operation_type_id, currency_id, merchant_id,
  datetime, provided, confirmation, order_id, status_id, status_modification_date,
  active_balance_before, reserved_balance_before, company_balance_before, company_commission_balance_before,
  source_type, source_id, provided_modification_date)
(SELECT W.id, CW.id, -WR.amount, 0, COM.id, 5, WR.currency_id, WR.merchant_id,
  WR.date_creation, 1, -1, NULL, 1, NULL,
  NULL, NULL, NULL, NULL,
  'WITHDRAW', WR.id, NULL
FROM WITHDRAW_REQUEST WR
JOIN WALLET W ON (W.user_id=WR.user_id AND W.currency_id=WR.currency_id)
JOIN USER ON USER.id = W.user_id
JOIN COMPANY_WALLET CW ON (CW.currency_id=WR.currency_id)
JOIN COMMISSION COM ON (COM.operation_type = 5 AND user_role = USER.roleid)
WHERE WR.transaction_id IS NOT NULL
);

/*get result after insertion*/
SELECT *
FROM TRANSACTION
WHERE operation_type_id = 5 AND source_type='WITHDRAW' and active_balance_before IS NULL;

для акцептованных надо поле чщдвук заполнить акцептором (для старых)
