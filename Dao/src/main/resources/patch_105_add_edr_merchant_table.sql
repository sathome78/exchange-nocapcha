CREATE TABLE EDC_MERCHANT_ACCOUNT (
  `address` VARCHAR(256) NOT NULL,
  `user_id` INT(11) NULL,
  `used` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`address`));

CREATE TABLE EDC_MERCHANT_TRANSACTION (
  `merchant_transaction_id` VARCHAR(256) NOT NULL,
  `transaction_id` INT(40) NOT NULL,
  `address` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`merchant_transaction_id`, `transaction_id`),
  INDEX `fk_EDC_MERCHANT_ACCOUNT_idx` (`address` ASC),
  INDEX `fk_TRANSACTION_idx` (`transaction_id` ASC),
  CONSTRAINT `fk_EDC_MERCHANT_ACCOUNT`
    FOREIGN KEY (`address`)
    REFERENCES EDC_MERCHANT_ACCOUNT (`address`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_TRANSACTION`
    FOREIGN KEY (`transaction_id`)
    REFERENCES TRANSACTION (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);