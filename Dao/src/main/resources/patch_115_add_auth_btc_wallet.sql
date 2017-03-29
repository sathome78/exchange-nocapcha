INSERT INTO ADMIN_AUTHORITY (id, name, description, hidden) VALUES (10, 'MANAGE_BTC_CORE_WALLET', NULL, 0);

INSERT INTO ADMIN_AUTHORITY_ROLE_DEFAULTS (role_id, admin_authority_id, enabled) SELECT id, 10, 0 FROM USER_ROLE
WHERE user_role_group_feature_id = 1;

INSERT INTO USER_ADMIN_AUTHORITY (user_id, admin_authority_id, enabled) SELECT id, 10, 0 FROM USER where roleid IN(SELECT id FROM USER_ROLE
WHERE user_role_group_feature_id = 1);