

create table BOT_LAUNCH_SETTINGS
(
  id int auto_increment
    primary key,
  bot_trader_id int not null,
  currency_pair_id int not null,
  is_enabled tinyint(1) default '0' not null,
  launch_interval_minutes int default '60' not null,
  create_timeout_seconds int default '3' not null,
  quantity_per_sequence int default '15' not null,
  constraint bot_launch_settings__uindex_bot_cp_ot
  unique (bot_trader_id, currency_pair_id),
  constraint bot_launch_settings___fk_bot_id
  foreign key (bot_trader_id) references BOT_TRADER (id),
  constraint bot_launch_settings___fk_currency_pair
  foreign key (currency_pair_id) references CURRENCY_PAIR (id)
)
;



INSERT INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id)
  SELECT BT.id, CP.id FROM BOT_TRADER BT JOIN CURRENCY_PAIR CP;


create table BOT_TRADING_SETTINGS
(
  id int auto_increment
    primary key,
  bot_launch_settings_id int not null,
  order_type_id int not null,
  min_amount decimal default '0' not null,
  max_amount decimal default '99999999' not null,
  min_price decimal default '0' null,
  max_price decimal default '99999999' not null,
  price_step decimal default '5' not null,
  price_growth_direction enum('UP', 'DOWN') default 'UP' not null,
  constraint bot_trading_settings__uindex_launch_ot
  unique (bot_launch_settings_id, order_type_id),
  constraint bot_trading_settings___fk_launch
  foreign key (bot_launch_settings_id) references BOT_LAUNCH_SETTINGS (id),
  constraint bot_trading_settings___fk_order_type
  foreign key (order_type_id) references ORDER_TYPE (id)
)
;


INSERT INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id)
  SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH JOIN ORDER_TYPE OT;

