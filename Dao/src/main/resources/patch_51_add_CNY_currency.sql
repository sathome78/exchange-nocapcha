
INSERT INTO `birzha`.`MERCHANT` (`description`, `name`) VALUES ('Invoice', 'Invoice');

INSERT INTO `birzha`.`CURRENCY` (`name`, `description`) VALUES ('CNY', 'CNY');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Invoice"),
          (SELECT id from CURRENCY WHERE name="CNY"),
          0.01000000);

INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Invoice')
, '/client/img/merchants/invoice.png', 'Invoice', (SELECT id from CURRENCY WHERE name='CNY'));

INSERT INTO DATABASE_PATCH VALUES('patch_51_added_Invoice_support',default,1);