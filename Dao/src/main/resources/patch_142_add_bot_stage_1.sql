
create table USER_ROLE_SETTINGS
(
  user_role_id int not null
    primary key,
  order_acception_same_role_only tinyint(1) default '0' not null,
  manual_change_allowed tinyint(1) default '1' not null,
  bot_acception_allowed tinyint(1) default '0' not null,
  constraint user_role_settings_user_role_id_fk
  foreign key (user_role_id) references USER_ROLE (id)
)
;

INSERT INTO USER_ROLE_BUSINESS_FEATURE (name) VALUES ('BOT');
INSERT INTO USER_ROLE_GROUP_FEATURE (name) VALUES ('BOT');

INSERT INTO USER_ROLE (name, user_role_business_feature_id, user_role_group_feature_id)
VALUES ('BOT_TRADER',
  (SELECT id FROM USER_ROLE_BUSINESS_FEATURE WHERE name = 'BOT'),
  (SELECT id FROM USER_ROLE_GROUP_FEATURE WHERE name = 'BOT'));

INSERT INTO USER_ROLE_SETTINGS(user_role_id, order_acception_same_role_only, manual_change_allowed, bot_acception_allowed)
VALUES ((SELECT id FROM USER_ROLE WHERE name = 'BOT_TRADER'), 0, 0, 1);



INSERT INTO COMMISSION(operation_type, value, date, user_role) SELECT OP.id, 0,
                                                                 CURRENT_TIMESTAMP, UR.id
                                                               FROM OPERATION_TYPE OP JOIN USER_ROLE UR WHERE UR.name = 'BOT_TRADER';
INSERT INTO CURRENCY_PAIR_LIMIT(currency_pair_id, user_role_id, order_type_id) 
  SELECT CP.id, (SELECT id FROM USER_ROLE WHERE name = 'BOT_TRADER'), OT.id FROM CURRENCY_PAIR CP JOIN ORDER_TYPE OT;


create table BOT_TRADER
(
  id int auto_increment
    primary key,
  user_id int null,
  is_enabled tinyint(1) default '0' not null,
  order_accept_timeout int default '5' not null
)
;

