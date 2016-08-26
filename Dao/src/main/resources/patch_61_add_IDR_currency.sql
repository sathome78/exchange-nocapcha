
INSERT INTO `birzha`.`CURRENCY` (`name`, `description`) VALUES ('IDR', 'IDR');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Invoice"),
          (SELECT id from CURRENCY WHERE name="IDR"),
          0.01000000);

INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Invoice')
, '/client/img/merchants/invoice.png', 'Invoice', (SELECT id from CURRENCY WHERE name='IDR'));

INSERT INTO WALLET (user_id, currency_id) select id, (select id from CURRENCY where name='IDR') from USER;

INSERT INTO DATABASE_PATCH VALUES('patch_61_added_IDR_Currency_support',default,1);