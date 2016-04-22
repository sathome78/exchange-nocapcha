DROP TABLE IF EXISTS EXORDERS ;

CREATE TABLE EXORDERS (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) NOT NULL,
  `currency_pair_id` int(11) NOT NULL,
  `operation_type_id` int(40) NOT NULL,
  `exrate` double(40,9) NOT NULL,
  `amount_base` double(40,9) NOT NULL,
  `amount_convert` double(40,9) NOT NULL,
  `commission_id` int(40) DEFAULT NULL,
  `commission_fixed_amount` double(40,9) NOT NULL,
  `user_acceptor_id` int(40) DEFAULT NULL,
  `date_creation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_acception` timestamp NULL DEFAULT NULL,
  `status_id` int(40) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `currency_pair` (`currency_pair_id`),
  KEY `fk_USER_ACCEPTOR` (`user_acceptor_id`),
  KEY `fk_OPERATION_TYPE` (`operation_type_id`),
  KEY `status` (`status_id`),
  KEY `commission_id` (`commission_id`),
  CONSTRAINT `fk_COMMISSION` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_CURRENCY_PAIR` FOREIGN KEY (`currency_pair_id`) REFERENCES `CURRENCY_PAIR` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_OPERATION_TYPE` FOREIGN KEY (`operation_type_id`) REFERENCES `OPERATION_TYPE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_STATUS` FOREIGN KEY (`status_id`) REFERENCES `ORDER_STATUS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_ACCEPTOR` FOREIGN KEY (`user_acceptor_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_USER_CREATOR` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO DATABASE_PATCH VALUES('patch_32_create_table_exorders',default,1);

COMMIT;
