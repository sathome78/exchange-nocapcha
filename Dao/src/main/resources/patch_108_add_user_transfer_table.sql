DROP TABLE IF EXISTS USER_TRANSFER ;

CREATE TABLE USER_TRANSFER (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_user_id` int(11) NOT NULL,
  `to_user_id` int(11) NOT NULL,
  `currency_id` int(11) NOT NULL,
  `amount` double(40,9) NOT NULL,
  `commission_amount` double(40,9) NOT NULL,
  `creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_user_trnasfer_from_user_id` (`from_user_id`),
  KEY `FK_user_trnasfer_to_user_id` (`to_user_id`),
  KEY `FK_user_trnasfer_currency_id` (`currency_id`),
  CONSTRAINT `FK_user_trnasfer_from_user_id` FOREIGN KEY (`from_user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_trnasfer_to_user_id` FOREIGN KEY (`to_user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_trnasfer_currency_id` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

COMMIT;