INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="advcashmoney"),
          (SELECT id from CURRENCY WHERE name="EUR"),
          0.01000000);

INSERT INTO `birzha`.`CURRENCY` (`name`) VALUES ('UAH');

INSERT INTO `birzha`.`MERCHANT` (`description`, `name`) VALUES ('LiqPay', 'liqpay');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="liqpay"),
          (SELECT id from CURRENCY WHERE name="UAH"),
          0.01000000);

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (1, 7, 'RUB/UAH') ;
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (2, 7, 'USD/UAH') ;
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (3, 7, 'EUR/UAH') ;
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (4, 7, 'BTC/UAH') ;
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (5, 7, 'LTC/UAH') ;
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (6, 7, 'EDRC/UAH') ;

INSERT INTO DATABASE_PATCH VALUES('patch_25_added_liqpay_and_currency_pairs',default,1);