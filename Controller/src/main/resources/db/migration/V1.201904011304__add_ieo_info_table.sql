CREATE TABLE IF NOT EXISTS IEO_INFO
(
  currency_id  INT(11)                                    NOT NULL,
  user_id      INT(11)                                    NOT NULL,
  rate         double(40, 9)                              not null,
  amount       double(40, 9)                              not null,
  contributors VARCHAR(254)                               NOT NULL,
  started      timestamp                                  NOT NULL,
  status       enum ('finish', 'pending', 'fail') NOT NULL,
  total_limit  double(40, 9)                              not null,
  buy_limit    double(40, 9)                              not null,
  version      int(8)                                     not null,
  PRIMARY KEY (currency_id),
  FOREIGN KEY (currency_id) REFERENCES CURRENCY (id),
  FOREIGN KEY (user_id) REFERENCES User (id)
)
  ENGINE = INNODB;