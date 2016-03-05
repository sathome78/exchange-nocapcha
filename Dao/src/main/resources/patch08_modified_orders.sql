CREATE TABLE IF NOT EXISTS `Birzha`.`ORDERS` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `wallet_id_sell` INT(40) NOT NULL,
  `currency_sell` INT(40) NOT NULL,
  `amount_sell` DOUBLE(40,9) NOT NULL,
  `commission_amount_sell` DOUBLE(40,9) NOT NULL,
  `wallet_id_buy` INT(40) NULL DEFAULT NULL,
  `currency_buy` INT(40) NOT NULL,
  `amount_buy` DOUBLE(40,9) NOT NULL,
  `commission_amount_buy` DOUBLE(40,9) NOT NULL,
  `operation_type` INT(40) NOT NULL,
  `status` INT(40) NOT NULL DEFAULT '1',
  `date_creation` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_final` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_order_UNIQUE` (`id` ASC),
  INDEX `fk_ORDERS_WALLET1_idx` (`wallet_id_sell` ASC),
  INDEX `fk_ORDER_CURRENCY1_idx` (`currency_buy` ASC),
  INDEX `fk_ORDER_OPERATION_TYPE1_idx` (`operation_type` ASC),
  INDEX `fk_ORDER_ORDER_STATUS1_idx` (`status` ASC),
  CONSTRAINT `fk_ORDERS_WALLET`
    FOREIGN KEY (`wallet_id_sell`)
    REFERENCES `Birzha`.`WALLET` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_CURRENCY`
    FOREIGN KEY (`currency_buy`)
    REFERENCES `Birzha`.`CURRENCY` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_OPERATION_TYPE`
    FOREIGN KEY (`operation_type`)
    REFERENCES `Birzha`.`OPERATION_TYPE` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_ORDER_STATUS`
    FOREIGN KEY (`status`)
    REFERENCES `Birzha`.`ORDER_STATUS` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = latin1