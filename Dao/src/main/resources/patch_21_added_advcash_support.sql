INSERT INTO `birzha`.`merchant` (`description`, `name`) VALUES ('Advcash Money', 'advcashmoney');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="advcashmoney"),
          (SELECT id from CURRENCY WHERE name="USD"),
          0.01000000);

INSERT INTO DATABASE_PATCH VALUES('patch_21_added_advcash_support',default,1);

SELECT * FROM BTC_TRANSACTION;