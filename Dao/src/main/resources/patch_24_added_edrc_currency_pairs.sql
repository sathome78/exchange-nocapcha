INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (6, 1, 'EDRC/RUB') ;
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (6, 2, 'EDRC/USD') ;
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (6, 3, 'EDRC/EUR') ;
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (6, 4, 'EDRC/BTC') ;
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name) VALUES (6, 5, 'EDRC/LTC') ;

INSERT INTO DATABASE_PATCH VALUES ('patch_24_added_edrc_currency_pairs', DEFAULT , 1);

SELECT *
FROM WITHDRAW_REQUEST;

SELECT *
FROM TRANSACTION WHERE id  = 877;