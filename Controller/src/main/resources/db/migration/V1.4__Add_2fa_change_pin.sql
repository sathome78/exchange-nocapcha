ALTER TABLE USER ADD COLUMN CHANGE_2FA_SETTING_PIN varchar(100);

INSERT INTO 2fa_notification_messages (event, type, message) VALUES ('CHANGE_2FA_SETTING', 'EMAIL', 'response.change2fa.pin.email');