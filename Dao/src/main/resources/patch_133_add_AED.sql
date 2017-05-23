INSERT INTO CURRENCY(name, description, hidden) VALUES
  ('AED', 'AED', 0);
INSERT INTO WALLET (user_id, currency_id)
  select id, (select id from CURRENCY where name='AED') from USER;

INSERT INTO COMPANY_WALLET (currency_id) SELECT id from CURRENCY WHERE name='AED';

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
VALUES ((SELECT id from MERCHANT WHERE name='Invoice'),
        (SELECT id from CURRENCY WHERE name='AED'),
        0.01);


INSERT INTO MERCHANT_IMAGE (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Invoice'),
                                  '/client/img/merchants/invoice.png', 'Invoice', (SELECT id from CURRENCY WHERE name='AED'));


INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT (select id from CURRENCY where name='AED'), OPERATION_TYPE.id, USER_ROLE.id, 40, NULL, 10
  FROM OPERATION_TYPE
    JOIN USER_ROLE
  WHERE OPERATION_TYPE.id IN (1, 2, 9) AND USER_ROLE.id != 5;


INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden)
VALUES(4, (SELECT id from CURRENCY WHERE name='AED'), 'BTC/AED', 221, 0),
  (2, (SELECT id from CURRENCY WHERE name='AED'), 'USD/AED', 222, 0);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CURRENCY_PAIR.id, USER_ROLE.id, ORDER_TYPE.id, 0, 99999999999
  FROM CURRENCY_PAIR
    JOIN USER_ROLE
    JOIN ORDER_TYPE
  WHERE CURRENCY_PAIR.name IN ('BTC/AED', 'USD/AED');