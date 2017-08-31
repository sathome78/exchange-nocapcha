ALTER TABLE user_role_settings ADD considered_for_price_range TINYINT(1) DEFAULT '0' NOT NULL;
ALTER TABLE bot_launch_settings ADD consider_user_orders TINYINT(1) DEFAULT 0 NOT NULL;
ALTER TABLE bot_launch_settings
  MODIFY COLUMN consider_user_orders TINYINT(1) NOT NULL DEFAULT 0 AFTER is_enabled;