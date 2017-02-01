
ALTER TABLE `birzha`.`MERCHANT`
ADD COLUMN `merchant_order` INT(2) NULL DEFAULT NULL AFTER `name`;

INSERT INTO DATABASE_PATCH VALUES('patch_86_merchant_order',default,1);