


INSERT INTO USER_ROLE_BUSINESS_FEATURE (name) VALUES ('BOT');
INSERT INTO USER_ROLE_GROUP_FEATURE (name) VALUES ('BOT');

INSERT INTO USER_ROLE (name, user_role_business_feature_id, user_role_group_feature_id)
VALUES ('BOT_TRADER',
  (SELECT id FROM USER_ROLE_BUSINESS_FEATURE WHERE name = 'BOT'),
  (SELECT id FROM USER_ROLE_GROUP_FEATURE WHERE name = 'BOT'));

INSERT INTO USER_ROLE_SETTINGS(user_role_id, order_acception_same_role_only, manual_change_allowed)
VALUES ((SELECT id FROM USER_ROLE WHERE name = 'BOT_TRADER'), 0, 0);



INSERT INTO COMMISSION(operation_type, value, date, user_role) SELECT OP.id, 0,
                                                                 CURRENT_TIMESTAMP, UR.id
                                                               FROM OPERATION_TYPE OP JOIN USER_ROLE UR WHERE UR.name = 'BOT_TRADER'