DROP TABLE IF EXISTS RIPPLE_TRANSACTION;

CREATE TABLE `RIPPLE_TRANSACTION` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NULL,
  `issuer_address` VARCHAR(256) NOT NULL,
  `destination_address` VARCHAR(256) NOT NULL,
  `issuer_secret` VARCHAR(256) NOT NULL,
  `tx_hash` VARCHAR(500) NULL UNIQUE,
  `blop` VARCHAR(1500) NOT NULL,
  `date_creation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_last_modification` timestamp NULL DEFAULT NULL,
  `tx_status` ENUM('CREATED', 'SIGNED', 'SUBMITTED', 'CONFIRMED', 'DECLINED', 'ERROR'),
  `tx_type` ENUM('WITHDRAW', 'TO_MAIN_ACCOUNT'),
  `transaction_id` INT(11) NULL,
  `amount` double(40,9) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX(`tx_hash`),
  KEY `ripple_transaction_user_id` (`user_id`),
  KEY `ripple_transaction_transaction_id` (`transaction_id`),
  CONSTRAINT `fk_ripple_transaction_USER_ISSUER` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ripple_transaction_TRANSACTION` FOREIGN KEY (`transaction_id`) REFERENCES `TRANSACTION` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `MERCHANT` (`description`, `name`, `transaction_source_type_id`, `service_bean_name`)
VALUES ('Ripple', 'Ripple', 2, 'rippleServiceImpl');
INSERT INTO `CURRENCY` (`name`, `description`, `hidden`) VALUES ('XRP', 'XRP', '0');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name='Ripple'),
          (SELECT id from CURRENCY WHERE name='XRP'),
          0.000001);

INSERT INTO `MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Ripple')
, '/client/img/merchants/ripple.png', 'Ripple', (SELECT id from CURRENCY WHERE name='XRP'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='XRP') from USER;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT (select id from CURRENCY where name = 'XRP'), operation_type_id, user_role_id, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE currency_id = (select id from CURRENCY where name = 'EDR');

INSERT INTO `COMPANY_WALLET` (`currency_id`) VALUES ((select id from CURRENCY where name = 'XRP'));

INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden) VALUES((select id from CURRENCY where name = 'XRP'), (select id from CURRENCY where name = 'USD'), 'XRP/USD', 260, 0);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN USER_ROLE UR
  JOIN ORDER_TYPE OT where CP.name='XRP/USD';

CREATE TRIGGER `RIPPLE_TRANSACTION_BEFORE_UPD_TR`
BEFORE UPDATE ON `RIPPLE_TRANSACTION`
FOR EACH ROW
  BEGIN
    IF (NEW.tx_status <> OLD.tx_status) THEN
      SET new.date_last_modification = CURRENT_TIMESTAMP;
    END IF;
  END