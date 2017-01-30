
ALTER TABLE `birzha`.`MERCHANT`
ADD COLUMN `merchant_order` INT(2) NULL DEFAULT NULL AFTER `name`;

INSERT INTO DATABASE_PATCH VALUES('patch_83_added_okpay_support',default,1);