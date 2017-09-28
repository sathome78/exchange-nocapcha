ALTER TABLE STOCK_EXCHANGE DROP link;
ALTER TABLE STOCK_EXCHANGE ADD is_active TINYINT(1) DEFAULT 1 NOT NULL;
ALTER TABLE STOCK_EXCHANGE ADD last_field_name VARCHAR(25) NULL;
ALTER TABLE STOCK_EXCHANGE ADD buy_field_name VARCHAR(25) NULL;
ALTER TABLE STOCK_EXCHANGE ADD sell_field_name VARCHAR(25) NULL;
ALTER TABLE STOCK_EXCHANGE ADD low_field_name VARCHAR(25) NULL;
ALTER TABLE STOCK_EXCHANGE ADD high_field_name VARCHAR(25) NULL;
ALTER TABLE STOCK_EXCHANGE ADD volume_field_name VARCHAR(25) NULL;


UPDATE STOCK_EXCHANGE SET buy_field_name = 'LastBuyPrice',
  sell_field_name = 'LastSellPrice',
  low_field_name = 'DailyBestBuyPrice',
  high_field_name = 'DailyBestSellPrice',
  volume_field_name = 'DailyTradedTotalVolume'
WHERE name = 'xBTCe';

UPDATE STOCK_EXCHANGE SET buy_field_name = 'bid',
  sell_field_name = 'ask',
  last_field_name = 'last_price',
  low_field_name = 'low',
  high_field_name = 'high',
  volume_field_name = 'volume'
WHERE name = 'BITFINEX';

UPDATE STOCK_EXCHANGE SET buy_field_name = 'bid',
  sell_field_name = 'ask',
  last_field_name = 'last',
  low_field_name = 'low',
  high_field_name = 'high',
  volume_field_name = 'volume'
WHERE name = 'Bitstamp';

UPDATE STOCK_EXCHANGE SET last_field_name = 'last',
  buy_field_name = 'highestBid',
  sell_field_name = 'lowestAsk',
  low_field_name = 'low24hr',
  high_field_name = 'high24hr',
  volume_field_name = 'baseVolume'
WHERE name = 'alcurEX';




create table STOCK_EXCHANGE_CURRENCY_ALIAS
(
  stock_exchange_id int not null,
  currency_id int not null,
  alias varchar(20) null,
  primary key (stock_exchange_id, currency_id),
  constraint stock_exchange_currency_alias___fk_st_ex_id
  foreign key (stock_exchange_id) references STOCK_EXCHANGE (id),
  constraint stock_exchange_currency_alias___fk_cur_id
  foreign key (currency_id) references CURRENCY (id)
)
;

CREATE INDEX stock_exrate__idx_cur_id_ex_id_date ON stock_exrate (currency_pair_id, stock_exchange_id, date);

-- update existing exchanges

INSERT INTO STOCK_CURRENCY_PAIR(stock_exchange_id, currency_pair_id) VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'EOS/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/BTC')),

  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XMR/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XMR/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'EOS/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'EOS/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'IOTA/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'IOTA/BTC')),

  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'EOS/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XLM/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XMR/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XMR/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/BTC')),

  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bitstamp'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bitstamp'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bitstamp'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bitstamp'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bitstamp'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bitstamp'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/BTC')),

  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'alcurEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'alcurEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'alcurEX'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DOGE/BTC'))

;


INSERT INTO STOCK_EXCHANGE_CURRENCY_ALIAS VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'xBTCe'), (SELECT id FROM CURRENCY WHERE name = 'DASH'), 'DSH'),

  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Kraken'), (SELECT id FROM CURRENCY WHERE name = 'BTC'), 'XBT'),

  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY WHERE name = 'DASH'), 'DSH'),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'BITFINEX'), (SELECT id FROM CURRENCY WHERE name = 'IOTA'), 'IOT');


-- end update existing exchanges

INSERT INTO STOCK_EXCHANGE (name, is_active, last_field_name, buy_field_name, sell_field_name, low_field_name, high_field_name, volume_field_name)
VALUES ('Poloniex', 0, 'last', 'highestBid', 'lowestAsk', 'low24hr', 'high24hr', 'baseVolume');
INSERT INTO STOCK_CURRENCY_PAIR(stock_exchange_id, currency_pair_id) VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DOGE/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LSK/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XEM/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XMR/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Poloniex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/BTC'));
  ;

INSERT INTO STOCK_EXCHANGE (name, is_active, last_field_name, buy_field_name, sell_field_name, low_field_name, high_field_name, volume_field_name)
VALUES ('Bittrex', 0, 'Last', 'Bid', 'Ask', 'Low', 'High', 'Volume');
INSERT INTO STOCK_CURRENCY_PAIR(stock_exchange_id, currency_pair_id) VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DOGE/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LSK/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XEM/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XLM/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XMR/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/BTC'));

INSERT INTO STOCK_EXCHANGE_CURRENCY_ALIAS VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Bittrex'), (SELECT id FROM CURRENCY WHERE name = 'BCH'), 'BCC');

INSERT INTO STOCK_EXCHANGE (name, is_active)
VALUES ('Gdax', 0);
INSERT INTO STOCK_CURRENCY_PAIR(stock_exchange_id, currency_pair_id) VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Gdax'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BTC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Gdax'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BTC/EUR')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Gdax'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Gdax'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Gdax'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Gdax'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC'));

INSERT INTO STOCK_EXCHANGE (name, is_active, last_field_name, buy_field_name, sell_field_name, low_field_name, high_field_name, volume_field_name)
VALUES ('CEXio', 0, 'last', 'bid', 'ask', 'low', 'high', 'volume');
INSERT INTO STOCK_CURRENCY_PAIR(stock_exchange_id, currency_pair_id) VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'CEXio'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BTC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'CEXio'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BTC/EUR')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'CEXio'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'CEXio'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'CEXio'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'CEXio'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'CEXio'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'CEXio'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/BTC'));

INSERT INTO STOCK_EXCHANGE (name, is_active, last_field_name, buy_field_name, sell_field_name, low_field_name, high_field_name, volume_field_name)
VALUES ('Binance', 0, 'lastPrice', 'bidPrice', 'askPrice', 'lowPrice', 'highPrice', 'volume');
INSERT INTO STOCK_CURRENCY_PAIR(stock_exchange_id, currency_pair_id) VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Binance'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Binance'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Binance'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Binance'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'IOTA/BTC'));

INSERT INTO STOCK_EXCHANGE_CURRENCY_ALIAS VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'Binance'), (SELECT id FROM CURRENCY WHERE name = 'BCH'), 'BCC');


INSERT INTO STOCK_EXCHANGE (name, is_active, last_field_name, buy_field_name, sell_field_name, low_field_name, high_field_name, volume_field_name)
VALUES ('HitBTC', 0, 'last', 'bid', 'ask', 'low', 'high', 'volume');
INSERT INTO STOCK_CURRENCY_PAIR(stock_exchange_id, currency_pair_id) VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BTC/EUR')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BTC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DASH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DOGE/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'DOGE/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'EOS/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'EOS/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETH/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'ETC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LSK/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'LTC/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XEM/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XEM/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XMR/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XMR/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'XRP/BTC')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/USD')),
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY_PAIR WHERE name = 'BCH/BTC'));

INSERT INTO STOCK_EXCHANGE_CURRENCY_ALIAS VALUES
  ((SELECT id FROM STOCK_EXCHANGE WHERE name = 'HitBTC'), (SELECT id FROM CURRENCY WHERE name = 'BCH'), 'BCC');


