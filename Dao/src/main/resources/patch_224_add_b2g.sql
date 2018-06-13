INSERT INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('Bitcoiin2Gen', 'B2G', 2, 'b2gServiceImpl', 'CRYPTO');
INSERT INTO `CURRENCY` (`name`, `description`, `hidden`, `max_scale_for_refill`, `max_scale_for_withdraw`, `max_scale_for_transfer`)
VALUES ('B2G', 'Bitcoiin2Gen', '0', 8, 8, 8);

INSERT INTO COMPANY_WALLET_EXTERNAL(currency_id) VALUES ((SELECT id from CURRENCY WHERE name='B2G'));

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name='B2G'),
          (SELECT id from CURRENCY WHERE name='B2G'),
          0.00000001);

INSERT INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='B2G')
, '/client/img/merchants/B2G.png', 'B2G', (SELECT id from CURRENCY WHERE name='B2G'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='B2G') from USER;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT (select id from CURRENCY where name = 'B2G'), operation_type_id, user_role_id, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDR');

INSERT INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = 'B2G'));

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, ticker_name)
VALUES((select id from CURRENCY where name = 'B2G'), (select id from CURRENCY where name = 'USD'), 'B2G/USD', 170, 0, 'B2G/USD');

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN USER_ROLE UR
  JOIN ORDER_TYPE OT where CP.name='B2G/USD';

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'B2G'), (select id from CURRENCY where name = 'BTC'), 'B2G/BTC', 160, 0, 'BTC', 'B2G/BTC');

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='B2G/BTC';

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market ,ticker_name)
VALUES((select id from CURRENCY where name = 'B2G'), (select id from CURRENCY where name = 'ETH'), 'B2G/ETH', 160, 0, 'ETH', 'B2G/ETH');

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='B2G/ETH';

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), (select id from CURRENCY where name = 'B2G'), 0.000001, 1, 1, 0);

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), (select id from CURRENCY where name = 'B2G'), 0.000001, 1, 1, 0);

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), (select id from CURRENCY where name = 'B2G'), 0.000001, 1, 1, 0);

INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), '/client/img/merchants/transfer.png', 'Transfer', (select id from CURRENCY where name = 'B2G'));

INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), '/client/img/merchants/voucher.png', 'Voucher', (select id from CURRENCY where name = 'B2G'));

INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), '/client/img/merchants/voucher_free.png', 'Free voucher', (select id from CURRENCY where name = 'B2G'));

INSERT INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id)
  SELECT BT.id, CP.id FROM BOT_TRADER BT
    JOIN CURRENCY_PAIR CP WHERE CP.name IN ('B2G/USD', 'B2G/BTC', 'B2G/ETH');

INSERT INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id)
  SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH
    JOIN ORDER_TYPE OT
  WHERE BLCH.currency_pair_id IN (SELECT id FROM CURRENCY_PAIR WHERE name IN ('B2G/USD', 'B2G/BTC', 'B2G/ETH'));

INSERT INTO MERCHANT_SPEC_PARAMETERS(merchant_id, param_name, param_value) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'B2G'), 'LastRecievedBlock', 477192);