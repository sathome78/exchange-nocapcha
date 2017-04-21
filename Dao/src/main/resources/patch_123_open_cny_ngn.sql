UPDATE CURRENCY SET hidden = 0 where id IN (8, 13);

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden) VALUES(4, 13, 'BTC/NGN', 212, 0);

UPDATE CURRENCY_PAIR SET hidden = 0 WHERE id IN (22, 27, 38);