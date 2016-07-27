CREATE TABLE INVOICE_REQUEST (
  `transaction_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `acceptance_user_id` INT NULL,
  `acceptance_time` DATETIME NULL,
  UNIQUE INDEX `transaction_id_UNIQUE` (`transaction_id` ASC));
ALTER TABLE INVOICE_REQUEST
ADD INDEX `inv_owner_id_fk_idx` (`user_id` ASC),
ADD INDEX `inv_accept_user_id_fk_idx` (`acceptance_user_id` ASC);
ALTER TABLE INVOICE_REQUEST
ADD CONSTRAINT `inv_owner_id_fk`
  FOREIGN KEY (`user_id`)
  REFERENCES USER (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `inv_transaction_id_fk`
  FOREIGN KEY (`transaction_id`)
  REFERENCES TRANSACTION (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `inv_accept_user_id_fk`
  FOREIGN KEY (`acceptance_user_id`)
  REFERENCES USER (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

INSERT INTO INVOICE_REQUEST (transaction_id, user_id)
SELECT tx.id AS transaction_id, u.id AS user_id FROM TRANSACTION AS tx
	INNER JOIN WALLET AS wlt ON tx.user_wallet_id = wlt.id
	INNER JOIN USER AS u ON wlt.user_id = u.id
    where merchant_id = 16;

	UPDATE INVOICE_REQUEST AS inv
	JOIN (SELECT id AS admin_id FROM USER where roleid = 1 limit 1) AS admin
    SET inv.acceptance_user_id = admin_id, acceptance_time = '2016-07-20 20:00:00'
    WHERE transaction_id IN (SELECT id FROM TRANSACTION where merchant_id = 16 and provided != 0);

INSERT INTO DATABASE_PATCH VALUES('patch_55_add_invoice_requests',default,1);