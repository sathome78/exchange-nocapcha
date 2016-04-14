
INSERT INTO `birzha`.`MERCHANT` (`description`, `name`) VALUES ('Nix Money', 'Nix Money');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Nix Money"),
          (SELECT id from CURRENCY WHERE name="USD"),
          0.01000000);

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name="Nix Money"),
          (SELECT id from CURRENCY WHERE name="EUR"),
          0.01000000);

INSERT INTO DATABASE_PATCH VALUES('patch_29_added_nixmoney_support',default,1);