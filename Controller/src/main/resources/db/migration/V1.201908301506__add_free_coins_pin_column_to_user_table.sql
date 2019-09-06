ALTER TABLE USER ADD COLUMN free_coins_pin VARCHAR(100) AFTER qubera_account_pin;

INSERT IGNORE INTO 2FA_NOTIFICATION_MESSAGES (event, type, message) VALUES ('FREE_COINS', 'EMAIL', 'response.freecoins.pin.email');
