CREATE TABLE YANDEX_MONEY_PAYMENT (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) NOT NULL,
  `amount` double(40,9) NOT NULL,
  `merchant_image_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `currency_id_idx` (`currency_id`),
  KEY `merchant_image_id_idx` (`merchant_image_id`),
  CONSTRAINT `currency_id` FOREIGN KEY (`currency_id`) REFERENCES CURRENCY (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `merchant_image_id` FOREIGN KEY (`merchant_image_id`) REFERENCES MERCHANT_IMAGE (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);
  INSERT INTO DATABASE_PATCH VALUES('patch_71_add_yandex_payments',default,1);