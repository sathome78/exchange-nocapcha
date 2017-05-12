
INSERT INTO CURRENCY(name, description, hidden) VALUES ('DASH', 'Dash', 0);
INSERT INTO WALLET (user_id, currency_id)
  select id, (select id from CURRENCY where name='DASH') from USER;


INSERT INTO MERCHANT (`description`, `name`, transaction_source_type_id, service_bean_name) VALUES ('Dash', 'Dash',
                                                                                 (SELECT id FROM TRANSACTION_SOURCE_TYPE WHERE name = 'BTC_INVOICE'),
                                                                                  'dashServiceImpl');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
VALUES ((SELECT id from MERCHANT WHERE name='Dash'),
        (SELECT id from CURRENCY WHERE name='DASH'),
        0.000000010);

INSERT INTO MERCHANT_IMAGE (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Dash')
  , '/client/img/merchants/dash.png', 'Dash', (SELECT id from CURRENCY WHERE name='DASH'));


INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, .CRYPTO_CORE_WALLET.title_code)
VALUES (18, 15, 'dashWallet.title');

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum, max_daily_request)
  SELECT (select id from CURRENCY where name='DASH'), OPERATION_TYPE.id, USER_ROLE.id, 0.1, NULL, 10
  FROM OPERATION_TYPE
    JOIN USER_ROLE
  WHERE OPERATION_TYPE.id IN (1, 2, 9) AND USER_ROLE.id != 5;