ALTER TABLE `birzha`.`MERCHANT_CURRENCY`
ADD COLUMN `merchant_commission` DOUBLE(40,9) NULL DEFAULT '0.000000000' AFTER `min_sum`;

UPDATE `birzha`.`MERCHANT_CURRENCY` SET `merchant_commission`='3.000000000' WHERE `merchant_id`='9' and`currency_id`='7';
UPDATE `birzha`.`MERCHANT_CURRENCY` SET `merchant_commission`='2.500000000' WHERE `merchant_id`='14' and`currency_id`='7';
