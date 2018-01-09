UPDATE MERCHANT SET name = 'BCX', description = 'BCX' WHERE name = 'BTCX';
UPDATE CURRENCY SET name = 'BCX' WHERE name = 'BTCX';
UPDATE CURRENCY_PAIR SET name = 'BCX/USD' WHERE name = 'BTCX/USD';
UPDATE CURRENCY_PAIR SET name = 'BCX/BTC' WHERE name = 'BTCX/BTC';

INSERT INTO CRYPTO_CORE_WALLET(merchant_id, currency_id, CRYPTO_CORE_WALLET.title_code)
VALUES ((SELECT id from MERCHANT WHERE name='B2X'), (select id from CURRENCY where name='B2X'), 'b2xWallet.title');