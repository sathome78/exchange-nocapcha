INSERT INTO OPERATION_TYPE (name, description) VALUES ('user_transfer', NULL);

INSERT INTO COMMISSION (operation_type, value, editable) 
VALUES ((SELECT id FROM OPERATION_TYPE where name = 'user_transfer'), 0, 1);

ALTER TABLE `TRANSACTION`
  CHANGE COLUMN `source_type` `source_type` ENUM('ORDER', 'MERCHANT', 'REFERRAL', 'ACCRUAL', 'MANUAL', 'USER') NULL DEFAULT NULL ;


INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_84_add_user_transfer', DEFAULT, 1);