
INSERT INTO `birzha`.`CURRENCY` (`name`, `description`) VALUES ('THB', 'THB');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Invoice"),
          (SELECT id from CURRENCY WHERE name="THB"),
          0.01000000);

INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Invoice')
, '/client/img/merchants/invoice.png', 'Invoice', (SELECT id from CURRENCY WHERE name='THB'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='THB') from USER;

INSERT INTO CURRENCY_PAIR VALUES (33, (select id from CURRENCY where name='BTC'), (select id from CURRENCY where name='THB'), 'BTC/THB', 200, 0);
INSERT INTO CURRENCY_PAIR VALUES (34, (select id from CURRENCY where name='EDR'), (select id from CURRENCY where name='THB'), 'EDR/THB', 201, 0);
INSERT INTO CURRENCY_PAIR VALUES (35, (select id from CURRENCY where name='EDRC'), (select id from CURRENCY where name='THB'), 'EDRC/THB', 202, 0);
INSERT INTO CURRENCY_PAIR VALUES (36, (select id from CURRENCY where name='USD'), (select id from CURRENCY where name='THB'), 'USD/THB', 203, 0);

INSERT INTO DATABASE_PATCH VALUES('patch_64_added_THB_Currency_support',default,1);