

CREATE TABLE 2FA_NOTIFICATOR
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `bean_name` VARCHAR(256) NOT NULL,
  `pay_type` ENUM('FREE', 'PREPAID_LIFETIME', 'PAY_FOR_EACH') NOT NULL,
  `enable` BOOLEAN NOT NULL DEFAULT TRUE ,
  `name` VARCHAR(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO 2FA_NOTIFICATOR VALUES (1, 'emailNotificatorServiceImpl', 'FREE', true, 'E-MAIL');
INSERT INTO 2FA_NOTIFICATOR VALUES (2, 'smsNotificatorServiceImpl', 'PAY_FOR_EACH', TRUE, 'SMS');
INSERT INTO 2FA_NOTIFICATOR VALUES (3, 'telegramNotificatorServiceImpl', 'PREPAID_LIFETIME', TRUE, 'TELEGRAM');



CREATE TABLE 2FA_NOTIFICATION_PRICE
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `notificator_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  `message_price` DECIMAL(40,9) DEFAULT NULL,
  `subscribe_price` DECIMAL(40,9) DEFAULT NULL,
  UNIQUE(`notificator_id`, `role_id`),
  INDEX `2FA_NOTIFICATION_PRICE_notificator_id` (`notificator_id`),
  INDEX `2FA_NOTIFICATION_PRICE_role_id` (`role_id`),
  CONSTRAINT `fk_2FA_NOTIFICATION_PRICE_notificator_id` FOREIGN KEY (`notificator_id`) REFERENCES `2FA_NOTIFICATOR` (`id`),
  CONSTRAINT `fk_2FA_NOTIFICATION_PRICE_role_id` FOREIGN KEY (`role_id`) REFERENCES `USER_ROLE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE TELEGRAM_SUBSCRIPTION
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `code` VARCHAR(64),
  `subscription_state` ENUM('SUBSCRIBED', 'WAIT_FOR_SUBSCRIBE') NOT NULL ,
  `user_account`  VARCHAR(64),
  `chat_id` LONG,
  UNIQUE(`user_id`),
  INDEX `TELEGRAM_SUBSCRIPTION_user_id` (`user_id`),
  CONSTRAINT `fk_TELEGRAM_SUBSCRIPTION_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SMS_SUBSCRIPTION
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `contact` VARCHAR(64),
  `delivery_price` DECIMAL(40,9),
  `subscribe_code` VARCHAR(64),
  `subscription_state` ENUM('SUBSCRIBED', 'WAIT_FOR_SUBSCRIBE') NOT NULL ,
  `new_price` DECIMAL(40,9),
  `new_contact` VARCHAR(64),
  UNIQUE(`user_id`),
  INDEX `2FA_SMS_SUBSCRIPTION_user_id` (`user_id`),
  CONSTRAINT `fk_2FA_SMS_SUBSCRIPTION_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE 2FA_USER_NOTIFICATION_MESSAGE_SETTINGS
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id`  INT NOT NULL,
  `notificator_id`  INT,
  `event_name` ENUM('LOGIN', 'WITHDRAW', 'TRANSFER') NOT NULL,
  UNIQUE (`user_id`, `event_name`),
  INDEX `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_user_id` (`user_id`),
  INDEX `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_notificator_id` (`notificator_id`),
  CONSTRAINT `fk_2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`),
  CONSTRAINT `fk_2FA_USER_NOTIFICATION_MESSAGE_SETTINGS_notificator_id` FOREIGN KEY (`notificator_id`) REFERENCES `2FA_NOTIFICATOR` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE 2FA_NOTIFICATION_MESSAGES
(
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `event` VARCHAR(64) NOT NULL,
  `type` VARCHAR(64) NOT NULL,
  `message` VARCHAR(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE USER ADD COLUMN `withdraw_pin` VARCHAR(100) NULL;
ALTER TABLE USER ADD COLUMN `transfer_pin` VARCHAR(100) NULL;
ALTER TABLE USER CHANGE `pin` `login_pin` VARCHAR(100) NULL;

INSERT INTO `2FA_USER_NOTIFICATION_MESSAGE_SETTINGS` (user_id, notificator_id, event_name)
  SELECT id, IF(use2fa = 0, NULL, 1) AS new_val, 'LOGIN' FROM USER;

CREATE PROCEDURE fillNotificationPrice()
  BEGIN
    DECLARE i int DEFAULT (SELECT min(id) FROM `2FA_NOTIFICATOR` WHERE id > 1);
    DECLARE k int DEFAULT (SELECT min(id) FROM `USER_ROLE`);
    WHILE i <= (SELECT max(id) FROM `2FA_NOTIFICATOR`) DO
      WHILE k <= (SELECT max(id) FROM `USER_ROLE`) DO
        INSERT INTO `2FA_NOTIFICATION_PRICE` (`notificator_id`, role_id, message_price, subscribe_price)
        VALUES (i, k, (case when (i=2) THEN 0 ELSE NULL END), (case when (i = 3) THEN 0 ELSE NULL END));
        SET k = (SELECT min(id) FROM `USER_ROLE` WHERE id > k);
      END WHILE;
      SET i = (SELECT min(id) FROM `2FA_NOTIFICATOR` WHERE id > i);
      SET k = (SELECT min(id) FROM `USER_ROLE`);
    END WHILE;
  END;

CALL fillNotificationPrice();

drop procedure if exists fillNotificationPrice;
