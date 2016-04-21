
INSERT INTO `birzha`.`MERCHANT` (`description`, `name`) VALUES ('Privat24', 'Privat24');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Privat24"),
          (SELECT id from CURRENCY WHERE name="UAH"),
          0.01000000);

INSERT INTO DATABASE_PATCH VALUES('patch_34_added_privat24_support',default,1);