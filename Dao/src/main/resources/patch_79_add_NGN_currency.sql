UPDATE `birzha`.`CURRENCY_PAIR` SET `currency1_id`='9', `currency2_id`='12', `name`='EDR/INR' WHERE `id`='37';

INSERT INTO `birzha`.`CURRENCY` (`name`, `description`, `min_withdraw_sum`) VALUES ('NGN', 'NGN', 1);

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Invoice"),
          (SELECT id from CURRENCY WHERE name="NGN"),
          0.01000000);

INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Invoice')
, '/client/img/merchants/invoice.png', 'Invoice', (SELECT id from CURRENCY WHERE name='NGN'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='NGN') from USER;

INSERT INTO CURRENCY_PAIR VALUES (38, (select id from CURRENCY where name='EDR'), (select id from CURRENCY where name='NGN'), 'EDR/NGN', 211, 0);


INSERT INTO DATABASE_PATCH VALUES('patch_79_added_NGN_Currency_support',default,1);