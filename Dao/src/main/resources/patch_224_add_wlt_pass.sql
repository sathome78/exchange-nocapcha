ALTER TABLE CRYPTO_CORE_WALLET ADD passphrase VARCHAR(64) NULL;

-- for prod - replace 'pass123' with actual passphrase!

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BTC')
                                                    AND merchant_id = (SELECT id FROM MERCHANT where name = 'Bitcoin');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'LTC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Litecoin');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'DASH')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Dash');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'ATB')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'ATB');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BCH')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Bitcoin Cash');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'DOGE')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Dogecoin');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BTG')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BTG');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'ZEC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Zcash');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'B2X')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'B2X');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BCD')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BCD');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'PBTC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Bitcoin');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'OCC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'OCC');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BCX')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BCX');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BTCZ')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BTCZ');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'LCC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'LCC');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BTX')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BTX');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BCI')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BCI');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BCA')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BitcoinAtom');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'SZC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'SZC');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'XBD')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BitDollar');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BTCP')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BTCP');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'ABTC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'AML');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'NSR')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'NuShares');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BBX')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BBX');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BEET')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BEET');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'NYC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'NYC');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'PTC')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'Perfectcoin');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'SABR')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'SABR');

UPDATE CRYPTO_CORE_WALLET SET passphrase = 'pass123' where currency_id = (SELECT id FROM CURRENCY where name = 'BRECO')
                                                           AND merchant_id = (SELECT id FROM MERCHANT where name = 'BRECO');


