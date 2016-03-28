INSERT INTO MERCHANT (description, name)
VALUES ("EDR Coin", "EDR Coin");

INSERT INTO CURRENCY (name) VALUES ("EDRC");

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="EDR Coin"),
          (SELECT id from CURRENCY WHERE name="EDRC"),
          0.00000001);

UPDATE MERCHANT_CURRENCY SET min_sum = 0.00000001 WHERE currency_id in (
  SELECT id from CURRENCY WHERE name IN ("EDRC","BTC")
);

INSERT INTO COMPANY_WALLET (currency_id, balance, commission_balance)
  VALUES ((SELECT id from CURRENCY WHERE name="EDRC"),0,0);

INSERT INTO DATABASE_PATCH VALUES('patch_20_added_edrc_support',default,1);