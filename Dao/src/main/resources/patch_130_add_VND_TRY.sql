INSERT INTO CURRENCY(name, description, hidden) VALUES
  ('VND', 'VND', 0), ('TRY', 'TRY', 0);
INSERT INTO WALLET (user_id, currency_id)
  select id, (select id from CURRENCY where name='VND') from USER;

INSERT INTO WALLET (user_id, currency_id)
  select id, (select id from CURRENCY where name='TRY') from USER;



INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
VALUES ((SELECT id from MERCHANT WHERE name='Invoice'),
        (SELECT id from CURRENCY WHERE name='VND'),
        0.01);

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
VALUES ((SELECT id from MERCHANT WHERE name='Invoice'),
        (SELECT id from CURRENCY WHERE name='TRY'),
        0.01);

INSERT INTO MERCHANT_IMAGE (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Invoice')
  , '/client/img/merchants/invoice.png', 'Invoice', (SELECT id from CURRENCY WHERE name='VND'));
INSERT INTO MERCHANT_IMAGE (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Invoice')
  , '/client/img/merchants/invoice.png', 'Invoice', (SELECT id from CURRENCY WHERE name='TRY'));


INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT (select id from CURRENCY where name='VND'), OPERATION_TYPE.id, USER_ROLE.id, 220000, NULL, 10
  FROM OPERATION_TYPE
    JOIN USER_ROLE
  WHERE OPERATION_TYPE.id IN (1, 2, 9) AND USER_ROLE.id != 5;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT (select id from CURRENCY where name='TRY'), OPERATION_TYPE.id, USER_ROLE.id, 30, NULL, 10
  FROM OPERATION_TYPE
    JOIN USER_ROLE
  WHERE OPERATION_TYPE.id IN (1, 2, 9) AND USER_ROLE.id != 5;

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden)
  VALUES(4, (SELECT id from CURRENCY WHERE name='VND'), 'BTC/VND', 215, 0),
    (9, (SELECT id from CURRENCY WHERE name='VND'), 'EDR/VND', 216, 0),
    (4, (SELECT id from CURRENCY WHERE name='TRY'), 'BTC/TRY', 217, 0),
    (9, (SELECT id from CURRENCY WHERE name='TRY'), 'EDR/TRY', 218, 0);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CURRENCY_PAIR.id, USER_ROLE.id, ORDER_TYPE.id, 0, 99999999999
FROM CURRENCY_PAIR
  JOIN USER_ROLE
 JOIN ORDER_TYPE
WHERE CURRENCY_PAIR.name IN ('BTC/VND', 'EDR/VND','BTC/TRY','EDR/TRY');