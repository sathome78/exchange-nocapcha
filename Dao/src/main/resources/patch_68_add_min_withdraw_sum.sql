ALTER TABLE CURRENCY
  ADD COLUMN `min_withdraw_sum` DOUBLE(40,9) NULL DEFAULT NULL AFTER `description`;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 645 WHERE id = 1;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 10 WHERE id = 2;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 9 WHERE id = 3;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 0.0165 WHERE id = 4;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 645 WHERE id = 5;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 35 WHERE id = 6;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 255 WHERE id = 7;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 65 WHERE id = 8;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 25 WHERE id = 9;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 131340 WHERE id = 10;
UPDATE CURRENCY SET CURRENCY.min_withdraw_sum = 345 WHERE id = 11;

INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_68_add_min_withdraw_sum', DEFAULT, 1);
