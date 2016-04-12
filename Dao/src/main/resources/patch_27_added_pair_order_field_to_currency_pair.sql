ALTER TABLE `currency_pair`
ADD COLUMN `pair_order` int(2);

UPDATE currency_pair
SET `pair_order` = 1 WHERE `name` = "BTC/USD";

UPDATE currency_pair
SET `pair_order` = 2 WHERE `name` = "EDRC/USD";

UPDATE currency_pair
SET `pair_order` = 3 WHERE `name` = "EDRC/RUR";
