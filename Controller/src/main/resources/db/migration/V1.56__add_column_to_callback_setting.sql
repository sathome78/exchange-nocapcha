DROP TABLE CALLBACK_SETTINGS;

CREATE TABLE IF NOT EXISTS `CALLBACK_SETTINGS` (
  `USER_ID`      int(11)      NOT NULL,
  `CALLBACK_URL` VARCHAR(100) NOT NULL,
  `PAIR_ID` INT(20) NOT NULL,
  PRIMARY KEY (`USER_ID`,`PAIR_ID`),
  FOREIGN KEY (`USER_ID`) REFERENCES USER (id),
  FOREIGN KEY (`PAIR_ID`) REFERENCES CURRENCY_PAIR(id)
) CHARACTER SET utf8 COLLATE utf8_general_ci;
