
INSERT INTO `birzha`.`MERCHANT` (`description`, `name`) VALUES ('Interkassa', 'Interkassa');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Interkassa"),
          (SELECT id from CURRENCY WHERE name="RUB"),
          0.01000000);

INSERT INTO DATABASE_PATCH VALUES('patch_41_added_interkassa_support',default,1);