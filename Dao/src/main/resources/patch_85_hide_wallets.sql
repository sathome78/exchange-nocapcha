ALTER TABLE CURRENCY
  ADD COLUMN `hidden` TINYINT(1) NULL DEFAULT 0 AFTER `min_withdraw_sum`;

UPDATE CURRENCY SET hidden = 1 WHERE name IN ('LTC', 'CNY', 'IDR', 'THB', 'NGN');
INSERT INTO DATABASE_PATCH VALUES ('patch_85_hide_wallets', default, 1);