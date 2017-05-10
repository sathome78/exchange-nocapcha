
UPDATE CURRENCY SET hidden = 0 where name='LTC';
INSERT INTO WALLET (user_id, currency_id)
  (select id, (select id from CURRENCY where name='LTC') from USER u WHERE NOT EXISTS(
      SELECT * FROM WALLET WHERE user_id = u.id AND currency_id = (select id from CURRENCY where name='LTC')));

INSERT INTO MERCHANT (`description`, `name`, transaction_source_type_id) VALUES ('Litecoin', 'Litecoin',
                                                                                 (SELECT id FROM TRANSACTION_SOURCE_TYPE WHERE name = 'BTC_INVOICE'));

UPDATE MERCHANT
SET name = 'Bitcoin', description = 'Bitcoin'
WHERE name = 'Blockchain';

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
VALUES ((SELECT id from MERCHANT WHERE name='Litecoin'),
        (SELECT id from CURRENCY WHERE name='LTC'),
        0.000000010);

INSERT INTO MERCHANT_IMAGE (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Litecoin')
  , '/client/img/merchants/litecoin.png', 'Litecoin', (SELECT id from CURRENCY WHERE name='LTC'));