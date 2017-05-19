ALTER TABLE `ETHEREUM_TEMP_ACCOUNT`
ADD COLUMN `merchant_id` INT(11) NULL DEFAULT NULL AFTER `public_key`,
ADD INDEX `fk_Merchant_idx` (`merchant_id` ASC);
ALTER TABLE `ETHEREUM_TEMP_ACCOUNT`
ADD CONSTRAINT `fk_Merchant`
  FOREIGN KEY (`merchant_id`)
  REFERENCES `MERCHANT` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

UPDATE ETHEREUM_TEMP_ACCOUNT set merchant_id = (select id from MERCHANT where name = 'Ethereum');

INSERT INTO `MERCHANT` (`description`, `name`) VALUES ('Ethereum Classic', 'Ethereum Classic');
INSERT INTO `CURRENCY` (`name`, `description`, `hidden`) VALUES ('ETC', 'ETC', '0');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Ethereum Classic"),
          (SELECT id from CURRENCY WHERE name="ETC"),
          0.000000010);

INSERT INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Ethereum Classic')
, '/client/img/merchants/ethereum_classic.png', 'Ethereum Classic', (SELECT id from CURRENCY WHERE name='ETC'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='ETC') from USER;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT (select id from CURRENCY where name = 'ETC'), operation_type_id, user_role_id, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'ETH');

INSERT INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = 'ETC'));

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden) VALUES((select id from CURRENCY where name = 'ETC'), (select id from CURRENCY where name = 'USD'), 'ETC/USD', 219, 0);
INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden) VALUES((select id from CURRENCY where name = 'ETC'), (select id from CURRENCY where name = 'BTC'), 'ETC/BTC', 220, 0);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN USER_ROLE UR
  JOIN ORDER_TYPE OT where CP.name='ETC/USD';

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN USER_ROLE UR
  JOIN ORDER_TYPE OT where CP.name='ETC/BTC';




