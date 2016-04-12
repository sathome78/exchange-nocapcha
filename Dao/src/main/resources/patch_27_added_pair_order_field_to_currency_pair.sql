ALTER TABLE `currency_pair`
ADD COLUMN `pair_order` int(2);

UPDATE currency_pair
SET `pair_order` = 1 WHERE `name` = "BTC/USD";

UPDATE currency_pair
SET `pair_order` = 2 WHERE `name` = "EDRC/USD";

UPDATE currency_pair
SET `pair_order` = 3 WHERE `name` = "EDRC/RUR";

INSERT INTO DATABASE_PATCH VALUES('patch_27_added_pair_order_field_to_currency_pair',default,1);