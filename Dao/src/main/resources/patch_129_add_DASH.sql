
INSERT INTO CURRENCY(name, description, hidden) VALUES ('DASH', 'Dash', 0);
INSERT INTO WALLET (user_id, currency_id)
  select id, (select id from CURRENCY where name='DASH') from USER;

INSERT INTO COMPANY_WALLET (currency_id) SELECT id from CURRENCY WHERE name='DASH';
INSERT INTO MERCHANT (`description`, `name`, transaction_source_type_id, service_bean_name) VALUES ('Dash', 'Dash',
                                                                                                    (SELECT id FROM TRANSACTION_SOURCE_TYPE WHERE name = 'BTC_INVOICE'),
                                                                                                    'dashServiceImpl');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
VALUES ((SELECT id from MERCHANT WHERE name='Dash'),
        (SELECT id from CURRENCY WHERE name='DASH'),
        0.000000010);

INSERT INTO MERCHANT_IMAGE (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Dash')
  , '/client/img/merchants/dash.png', 'Dash', (SELECT id from CURRENCY WHERE name='DASH'));


INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='Dash'), (select id from CURRENCY where name='DASH'), 'dashWallet.title');

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT (select id from CURRENCY where name='DASH'), OPERATION_TYPE.id, USER_ROLE.id, 0.1, NULL, 10
  FROM OPERATION_TYPE
    JOIN USER_ROLE
  WHERE OPERATION_TYPE.id IN (1, 2, 9) AND USER_ROLE.id != 5;

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden)
VALUES((SELECT id from CURRENCY WHERE name='DASH'), 2, 'DASH/USD', 217, 0),
  ((SELECT id from CURRENCY WHERE name='DASH'), 4, 'DASH/BTC', 218, 0);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CURRENCY_PAIR.id, USER_ROLE.id, ORDER_TYPE.id, 0, 99999999999
  FROM CURRENCY_PAIR
    JOIN USER_ROLE
    JOIN ORDER_TYPE
  WHERE CURRENCY_PAIR.name IN ('DASH/USD', 'DASH/BTC');