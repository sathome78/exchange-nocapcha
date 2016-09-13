ALTER TABLE `birzha`.`BTC_TRANSACTION`
ADD COLUMN `acceptance_user_id` INT(11) NULL DEFAULT NULL AFTER `transaction_id`,
ADD COLUMN `acceptance_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP AFTER `acceptance_user_id`;

INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_66_add_bitcoin_accepter_and_date', DEFAULT, 1);
