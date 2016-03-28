INSERT INTO `birzha`.`merchant` (`id`, `description`, `name`) VALUES ('4', 'Advcash Money', 'advcashmoney');
INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum) VALUES (4,2,0.0100);

INSERT INTO DATABASE_PATCH VALUES('patch_21_added_advcash_support',default,1);
