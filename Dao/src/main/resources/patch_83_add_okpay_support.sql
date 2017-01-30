
INSERT INTO `birzha`.`MERCHANT` (`description`, `name`) VALUES ('OkPay', 'OkPay');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="OkPay"),
          (SELECT id from CURRENCY WHERE name="USD"),
          0.01000000);

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="OkPay"),
          (SELECT id from CURRENCY WHERE name="EUR"),
          0.01000000);

INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='OkPay')
, '/client/img/merchants/okpay.png', 'Invoice', (SELECT id from CURRENCY WHERE name='USD'));

INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='OkPay')
, '/client/img/merchants/okpay.png', 'Invoice', (SELECT id from CURRENCY WHERE name='EUR'));


INSERT INTO DATABASE_PATCH VALUES('patch_83_added_okpay_support',default,1);