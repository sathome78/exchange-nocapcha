ALTER TABLE EDC_TEMP_ACCOUNT
ADD COLUMN `account_id` VARCHAR(256) NULL AFTER `brain_priv_key`;
ADD COLUMN `account_name` VARCHAR(256) NOT NULL AFTER `account_id`;
ADD COLUMN `used` TINYINT(1) NOT NULL DEFAULT '0' AFTER `account_name`;