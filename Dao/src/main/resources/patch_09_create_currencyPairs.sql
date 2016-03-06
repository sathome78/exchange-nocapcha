
DROP TABLE IF EXISTS `currency_pairs`;
CREATE TABLE IF NOT EXISTS `birzha`.`currency_pairs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency1_id` int(11) NOT NULL,
  `currency2_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

INSERT INTO `currency_pairs` VALUES (1,2,1,'USD/RUR');
INSERT INTO DATABASE_PATCH (version,patched) VALUES ("patch_09_create_currencyPairs",1);