
UPDATE CURRENCY SET hidden = 0 where name='LTC';
INSERT INTO WALLET (user_id, currency_id)
  (select id, (select id from CURRENCY where name='LTC') from USER u WHERE NOT EXISTS(
      SELECT * FROM WALLET WHERE user_id = u.id AND currency_id = (select id from CURRENCY where name='LTC')));

INSERT INTO MERCHANT (`description`, `name`, transaction_source_type_id, service_bean_name) VALUES ('Litecoin', 'Litecoin',
                                                                                 (SELECT id FROM TRANSACTION_SOURCE_TYPE WHERE name = 'BTC_INVOICE'),
                                                                                  'litecoinServiceImpl');

UPDATE MERCHANT
SET name = 'Bitcoin', description = 'Bitcoin'
WHERE name = 'Blockchain';

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
VALUES ((SELECT id from MERCHANT WHERE name='Litecoin'),
        (SELECT id from CURRENCY WHERE name='LTC'),
        0.000000010);

INSERT INTO MERCHANT_IMAGE (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Litecoin')
  , '/client/img/merchants/litecoin.png', 'Litecoin', (SELECT id from CURRENCY WHERE name='LTC'));

create table CRYPTO_CORE_WALLET
(
  id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  merchant_id int null,
  currency_id int null,
  title_code varchar(20) null,
  constraint crypto_core_wallet__index_uq
  unique (merchant_id),
  constraint crypto_core_wallet___fk_merch_id
  foreign key (merchant_id) references MERCHANT (id),
  constraint crypto_core_wallet___fk_curr_id
  foreign key (currency_id) references CURRENCY (id)
)
;

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, .CRYPTO_CORE_WALLET.title_code)
VALUES (3, 4, 'btcWallet.title'), (17, 5, 'ltcWallet.title');

UPDATE CURRENCY_PAIR SET hidden = 0, pair_order = 215 WHERE name = 'LTC/BTC';
UPDATE CURRENCY_PAIR SET hidden = 0, pair_order = 216 WHERE name = 'LTC/USD';
