ALTER TABLE TRANSACTION
ADD COLUMN
(active_balance_before double(40,9) DEFAULT NULL,
reserved_balance_before double(40,9) DEFAULT NULL,
company_balance_before  double(40,9) DEFAULT NULL,
company_commission_balance_before  double(40,9) DEFAULT NULL,
source_type enum('ORDER','MERCHANT','REFERAL','ACCRUAL') DEFAULT NULL,
source_id INT(40) DEFAULT NULL);

ALTER TABLE TRANSACTION
ADD KEY source_type_source_id (`source_type`,`source_id`);

INSERT INTO OPERATION_TYPE (id, name, description)
VALUES (5, 'wallet_inner_transfer', 'between active and reserved balance');

INSERT INTO COMMISSION (operation_type, value)
VALUES (5, 0);

INSERT INTO OPERATION_TYPE (id, name, description)
VALUES (7, 'storno', 'for storno operation');

INSERT INTO COMMISSION (operation_type, value)
VALUES (7, 0);


INSERT INTO DATABASE_PATCH VALUES('patch_43_added_for_transaction',default,1);