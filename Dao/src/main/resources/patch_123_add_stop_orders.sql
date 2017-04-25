DROP TABLE IF EXISTS STOP_ORDERS ;

CREATE TABLE STOP_ORDERS (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `user_id` int(40) NOT NULL,
  `child_order_id` int(40) NOT NULL,
  `currency_pair_id` int(11) NOT NULL,
  `operation_type_id` int(40) NOT NULL,
  `stop` double(40,9) NOT NULL,
  `limit` double(40,9) NOT NULL,
  `amount_base` double(40,9) NOT NULL,
  `amount_convert` double(40,9) NOT NULL,
  `commission_id` int(40) DEFAULT NULL,
  `commission_fixed_amount` double(40,9) NOT NULL,
  `date_creation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modification` timestamp NULL DEFAULT NULL,
  `status_id` int(40) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `stop_orders_user_id` (`user_id`),
  KEY `stop_orders_currency_pair` (`currency_pair_id`),
  KEY `stop_orders_OPERATION_TYPE` (`operation_type_id`),
  KEY `stop_orders_status` (`status_id`),
  KEY `stop_orders_commission_id` (`commission_id`),
  KEY `stop_orders_child_order_id` (`child_order_id`),
  CONSTRAINT `fk_stop_orders_COMMISSION` FOREIGN KEY (`commission_id`) REFERENCES `COMMISSION` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_CURRENCY_PAIR` FOREIGN KEY (`currency_pair_id`) REFERENCES `CURRENCY_PAIR` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_OPERATION_TYPE` FOREIGN KEY (`operation_type_id`) REFERENCES `OPERATION_TYPE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_ORDER_STATUS` FOREIGN KEY (`status_id`) REFERENCES `ORDER_STATUS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_USER_CREATOR` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_stop_orders_CHILD_ORDER` FOREIGN KEY (`child_order_id`) REFERENCES `EXORDERS` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO DATABASE_PATCH VALUES('patch_123_add_stop_orders',default,1);

COMMIT;

CREATE TRIGGER `STOP_ORDERS_BEFORE_UPD_TR`
BEFORE UPDATE ON `stop_orders`
FOR EACH ROW
  BEGIN
    IF (NEW.status_id <> OLD.status_id) THEN
      SET new.date_modification = CURRENT_TIMESTAMP;
    END IF;
  END


