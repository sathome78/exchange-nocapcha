CREATE TABLE IF NOT EXISTS `CALLBACK_SETTINGS` (
  `USER_ID`      int(11)      NOT NULL AUTO_INCREMENT,
  `CALLBACK_URL` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `id_UNIQUE` (`USER_ID`),
  FOREIGN KEY (USER_ID) REFERENCES USER (id)
)
  CHARSET = utf8;
