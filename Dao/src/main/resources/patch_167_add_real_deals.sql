ALTER TABLE user_role_settings ADD use_real_money TINYINT(1) DEFAULT 1 NOT NULL;
UPDATE USER_ROLE_SETTINGS SET use_real_money = 0 WHERE user_role_id IN (8, 10);