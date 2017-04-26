CREATE TABLE `ETHEREUM_TEMP_ACCOUNT` (
  `address` VARCHAR(256) NOT NULL,
  `user_id` INT(11) NULL,
  `private_key` VARCHAR(256) NULL,
  `public_key` VARCHAR(256) NULL,
  PRIMARY KEY (`address`));

INSERT INTO `MERCHANT` (`description`, `name`) VALUES ('Ethereum', 'Ethereum');
INSERT INTO `CURRENCY` (`name`, `description`, `hidden`) VALUES ('ETH', 'ETH', '0');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Ethereum"),
          (SELECT id from CURRENCY WHERE name="ETH"),
          0.000000010);

INSERT INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Ethereum')
, '/client/img/merchants/ethereum.png', 'Ethereum', (SELECT id from CURRENCY WHERE name='ETH'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='ETH') from USER;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT (select id from CURRENCY where name = 'ETH'), operation_type_id, user_role_id, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDR');

INSERT INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = 'ETH'));

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden) VALUES((select id from CURRENCY where name = 'ETH'), (select id from CURRENCY where name = 'USD'), 'ETH/USD', 213, 0);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN USER_ROLE UR
  JOIN ORDER_TYPE OT where CP.name='ETH/USD';

