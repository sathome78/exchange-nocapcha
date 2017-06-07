INSERT INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`)
VALUES ('Stellar', 'Stellar', 2, 'stellarServiceImpl');
INSERT INTO `CURRENCY` (`name`, `description`, `hidden`) VALUES ('XLM', 'XLM', '0');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name='Stellar'),
          (SELECT id from CURRENCY WHERE name='XLM'),
          0.000001);

INSERT INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Stellar')
, '/client/img/merchants/stellar.png', 'Stellar', (SELECT id from CURRENCY WHERE name='XLM'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='XLM') from USER;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT (select id from CURRENCY where name = 'XLM'), operation_type_id, user_role_id, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDR');

INSERT INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = 'XLM'));

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden) VALUES((select id from CURRENCY where name = 'XLM'), (select id from CURRENCY where name = 'USD'), 'XLM/USD', 260, 0);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN USER_ROLE UR
  JOIN ORDER_TYPE OT where CP.name='XLM/USD';