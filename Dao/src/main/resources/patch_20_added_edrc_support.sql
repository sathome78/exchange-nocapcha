INSERT INTO MERCHANT (description, name)
VALUES ("EDR Coin", "EDR Coin");

INSERT INTO CURRENCY (name) VALUES ("EDRC");

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum) VALUES (5,6,0.0001);

INSERT INTO WALLET (currency_id, user_id, active_balance, reserved_balance)
VALUES (6, 1, 1, 0);

INSERT INTO COMPANY_WALLET (currency_id, balance, commission_balance) VALUES (6,1,0);

UPDATE MERCHANT_CURRENCY SET min_sum = 0.00000001 WHERE merchant_id in (3,5);

SELECT * FROM PENDING_BLOCKCHAIN_PAYMENT;