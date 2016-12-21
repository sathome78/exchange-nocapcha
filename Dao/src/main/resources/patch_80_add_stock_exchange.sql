CREATE TABLE `STOCK_EXCHANGE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `link` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);

CREATE TABLE STOCK_CURRENCY_PAIR
(
  stock_exchange_id INT(11) NOT NULL,
  currency_pair_id INT(11) NOT NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (stock_exchange_id, currency_pair_id),
  CONSTRAINT fk_stock_curr_pair_stock_ex_id FOREIGN KEY (stock_exchange_id) REFERENCES STOCK_EXCHANGE (id),
  CONSTRAINT fk_stock_corrency_pair_id FOREIGN KEY (currency_pair_id) REFERENCES CURRENCY_PAIR (id)
);
CREATE INDEX fk_stock_corrency_pair_id_idx ON STOCK_CURRENCY_PAIR (currency_pair_id);

CREATE TABLE STOCK_EXRATE
(
  id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  currency_pair_id INT(11) NOT NULL,
  stock_exchange_id INT(11) NOT NULL,
  date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  price_buy DOUBLE(40,9) NOT NULL,
  price_sell DOUBLE(40,9) NOT NULL,
  price_low DOUBLE(40,9) NOT NULL,
  price_high DOUBLE(40,9) NOT NULL,
  volume DOUBLE(40,9) NOT NULL,
  CONSTRAINT fk_stock_currency_pair_id FOREIGN KEY (currency_pair_id) REFERENCES CURRENCY_PAIR (id),
  CONSTRAINT fk_stock_exchange_id FOREIGN KEY (stock_exchange_id) REFERENCES STOCK_EXCHANGE (id)
);
CREATE INDEX fk_stock_currency_pair_id_idx ON STOCK_EXRATE (currency_pair_id);
CREATE INDEX fk_stock_exchange_id_idx ON STOCK_EXRATE (stock_exchange_id);

INSERT INTO STOCK_EXCHANGE (name, link) VALUES ('xBTCe', 'https://www.xbtce.com/');
INSERT INTO STOCK_EXCHANGE (name, link) VALUES ('BITFINEX', 'https://www.bitfinex.com/');
INSERT INTO STOCK_EXCHANGE (name, link) VALUES ('Kraken', 'https://www.kraken.com/');
INSERT INTO STOCK_EXCHANGE (name, link) VALUES ('Bitstamp', 'https://www.bitstamp.net/');
INSERT INTO STOCK_EXCHANGE (name, link) VALUES ('BTC-E', 'https://btc-e.com/');
INSERT INTO STOCK_EXCHANGE (name, link) VALUES ('YoBit', 'https://yobit.net/');
INSERT INTO STOCK_EXCHANGE (name, link) VALUES ('CoinsBank', 'https://coinsbank.com/');
INSERT INTO STOCK_EXCHANGE (name, link) VALUES ('alcurEX', 'https://alcurex.com/');
INSERT INTO STOCK_EXCHANGE (name, link) VALUES ('Yuanbao', 'https://www.yuanbao.com/');