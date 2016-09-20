ALTER TABLE WITHDRAW_REQUEST
  ADD COLUMN `status` INT(40) NULL DEFAULT 1 AFTER `merchant_image_id`;
UPDATE WITHDRAW_REQUEST SET status = 2, acceptance = acceptance WHERE processed_by IS NOT NULL;

INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_67_add_status_to_withdraw_request', DEFAULT, 1);
