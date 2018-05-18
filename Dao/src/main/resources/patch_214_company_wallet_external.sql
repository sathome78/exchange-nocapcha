CREATE TABLE COMPANY_WALLET_EXTERNAL (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `currency_id` INT(40) NOT NULL,
  `reserve_wallet_balance` DOUBLE(40,9) NULL DEFAULT '0.000000000',
  `cold_wallet_balance` DOUBLE(40,9) NULL DEFAULT '0.000000000',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_WALLET_EX_CURRENCIES_idx` (`currency_id` ASC),
  CONSTRAINT `fk_WALLET_EX_CURRENCIES`
    FOREIGN KEY (`currency_id`)
    REFERENCES CURRENCY (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

INSERT INTO COMPANY_WALLET_EXTERNAL(currency_id) SELECT id FROM CURRENCY;