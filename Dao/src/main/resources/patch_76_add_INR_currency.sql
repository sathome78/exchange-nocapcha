
INSERT INTO `birzha`.`CURRENCY` (`name`, `description`, 1) VALUES ('INR', 'INR');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Invoice"),
          (SELECT id from CURRENCY WHERE name="INR"),
          0.01000000);

INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Invoice')
, '/client/img/merchants/invoice.png', 'Invoice', (SELECT id from CURRENCY WHERE name='INR'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='INR') from USER;

INSERT INTO CURRENCY_PAIR VALUES (37, (select id from CURRENCY where name='INR'), (select id from CURRENCY where name='EDR'), 'INR/EDR', 210, 0);


INSERT INTO DATABASE_PATCH VALUES('patch_76_added_INR_Currency_support',default,1);