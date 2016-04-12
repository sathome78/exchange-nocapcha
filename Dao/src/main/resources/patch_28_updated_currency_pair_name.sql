UPDATE CURRENCY_PAIR
SET `name` = "EDRC/RUR" WHERE `name` = "EDRC/RUB";

INSERT INTO DATABASE_PATCH VALUES('patch_28_updated_currency_pair_name',default,1);

COMMIT;