
INSERT INTO `birzha`.`MERCHANT` (`description`, `name`) VALUES ('Yandex kassa', 'Yandex kassa');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Yandex kassa"),
          (SELECT id from CURRENCY WHERE name="RUB"),
          0.01000000);

INSERT INTO DATABASE_PATCH VALUES('patch_31_added_yandex_kassa_support',default,1);