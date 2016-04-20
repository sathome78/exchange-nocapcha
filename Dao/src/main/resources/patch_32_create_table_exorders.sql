DROP TABLE IF EXISTS EXORDERS ;

CREATE TABLE `transaction` (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_wallet_id` int(11) NOT NULL,
  `company_wallet_id` int(11) NOT NULL,
  `amount` double(40,9) NOT NULL,
  `commission_amount` double(40,9) NOT NULL,
  `commission_id` int(11) NOT NULL,
  `operation_type_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `merchant_id` int(11) DEFAULT NULL,
  `datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `provided` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_company_UNIQUE` (`id`),
  KEY `fk_COMPANY_ACCOUNT_WALLET1_idx` (`user_wallet_id`),
  KEY `COMPANY_ACCOUNT` (`commission_id`),
  KEY `TRANSACTION_CURRENCY_ID` (`currency_id`),
  KEY `Merchant_id` (`merchant_id`),
  KEY `TRANSACTION` (`operation_type_id`),
  KEY `TRANSACTION_COMPANY_WALLET` (`company_wallet_id`),
  CONSTRAINT `fk_COMPANY_ACCOUNT_WALLET1` FOREIGN KEY (`user_wallet_id`) REFERENCES `wallet` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`commission_id`) REFERENCES `commission` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_3` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_4` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_5` FOREIGN KEY (`operation_type_id`) REFERENCES `operation_type` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_6` FOREIGN KEY (`company_wallet_id`) REFERENCES `company_wallet` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

INSERT INTO DATABASE_PATCH VALUES('patch_32_create_table_exorders',default,1);

COMMIT;
