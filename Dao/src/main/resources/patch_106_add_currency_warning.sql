CREATE TABLE `CURRENCY_WARNING` (
  `currency_id` int(11) NOT NULL,
  `warning_code` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`currency_id`),
  CONSTRAINT `currency_warning___fk_curr_id` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  INSERT INTO CURRENCY_WARNING (currency_id, warning_code) VALUES
    (4, 'input.warning.BTC'),
    (9, 'input.warning.EDR');