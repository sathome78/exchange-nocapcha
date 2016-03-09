INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum) VALUES (1,1,1);
INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum) VALUES (2,3,0.01);
INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum) VALUES (2,2,0.01);

ALTER TABLE WALLET CHANGE COLUMN active_balance active_balance DOUBLE(40,9) DEFAULT 0;
UPDATE WALLET SET active_balance = 0 WHERE active_balance IS NULL;

INSERT DATABASE_PATCH VALUES('patch_14_added_data_merchant_currency',default,1);