INSERT INTO ADMIN_AUTHORITY (id, name, description, hidden)
VALUES (11, 'SEE_REPORTS', NULL, 0);

INSERT INTO USER_ADMIN_AUTHORITY (user_id, admin_authority_id, enabled)
SELECT DISTINCT(user_id) user_id, 11, 0 FROM USER_ADMIN_AUTHORITY