ALTER TABLE `MERCHANT_CURRENCY`
  ADD COLUMN `refill_block` TINYINT(1) NULL DEFAULT '0' AFTER `withdraw_block`;

INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_79_add_refill_block', DEFAULT, 1);
