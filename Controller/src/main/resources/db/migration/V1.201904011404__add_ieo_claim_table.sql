CREATE TABLE IF NOT EXISTS IEO_CLAIM
(
  id            INT(11) PRIMARY KEY                                              NOT NULL AUTO_INCREMENT,
  currency_name VARCHAR(64)                                                      NOT NULL,
  maker_id      INT(11)                                                          not null,
  user_id       INT(11)                                                          not null,
  amount        double(40, 9)                                                    not null,
  created       timestamp default NOW()                                          NOT NULL,
  state         enum ('created', 'processed') default 'created'                  NOT NULL,
  FOREIGN KEY (user_id) REFERENCES User (id),
  FOREIGN KEY (maker_id) REFERENCES User (id)
)
  ENGINE = INNODB;