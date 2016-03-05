ALTER TABLE `birzha`.`ORDERS` 
CHANGE COLUMN `date_final` `date_final` TIMESTAMP NULL DEFAULT NULL ;

INSERT INTO DATABASE_PATCH (version,patched) VALUES ("patch_10_modified_orders_dates",0);