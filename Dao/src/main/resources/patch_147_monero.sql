INSERT INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`, `process_type`)
VALUES ('Monero', 'Monero', 2, 'moneroServiceImpl', 'CRYPTO');
INSERT INTO `CURRENCY` (`name`, `description`, `hidden`, `max_scale_for_refill`, `max_scale_for_withdraw`, `max_scale_for_transfer`)
VALUES ('XMR', 'Monero', '0', 8, 8, 8);

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name='Monero'),
          (SELECT id from CURRENCY WHERE name='XMR'),
          0.000001);

INSERT INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Monero')
, '/client/img/merchants/monero.png', 'Monero', (SELECT id from CURRENCY WHERE name='XMR'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='XMR') from USER;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT (select id from CURRENCY where name = 'XMR'), operation_type_id, user_role_id, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDR');

INSERT INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = 'XMR'));

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden) VALUES((select id from CURRENCY where name = 'XMR'), (select id from CURRENCY where name = 'USD'), 'XMR/USD', 282, 0);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN USER_ROLE UR
  JOIN ORDER_TYPE OT where CP.name='XMR/USD';

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market)
VALUES((select id from CURRENCY where name = 'XMR'), (select id from CURRENCY where name = 'BTC'), 'XMR/BTC', 272, 0, 'BTC');

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
    JOIN USER_ROLE UR
    JOIN ORDER_TYPE OT where CP.name='XMR/BTC';

ALTER TABLE REFILL_REQUEST_ADDRESS
CHANGE COLUMN `address` `address` VARCHAR(128) NOT NULL ;


INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='Monero'), (select id from CURRENCY where name='XMR'), 'bchWallet.title');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), (select id from CURRENCY where name = 'XMR'), 0.000001, 1, 1, 0);

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), (select id from CURRENCY where name = 'XMR'), 0.000001, 1, 1, 0);

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
VALUES ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), (select id from CURRENCY where name = 'XMR'), 0.000001, 1, 1, 0);

INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'SimpleTransfer'), '/client/img/merchants/transfer.png', 'Transfer', (select id from CURRENCY where name = 'XMR'));

INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'VoucherTransfer'), '/client/img/merchants/voucher.png', 'Voucher', (select id from CURRENCY where name = 'XMR'));

INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES
  ((SELECT id FROM MERCHANT WHERE name = 'VoucherFreeTransfer'), '/client/img/merchants/voucher_free.png', 'Free voucher', (select id from CURRENCY where name = 'XMR'));
