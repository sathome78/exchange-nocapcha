INSERT INTO ADMIN_AUTHORITY(id, name, description, hidden) VALUES (9, 'EDIT_USER', NULL, 0);

INSERT INTO ADMIN_AUTHORITY_ROLE_DEFAULTS(role_id, admin_authority_id, enabled) VALUES (1, 9, 1);
INSERT INTO ADMIN_AUTHORITY_ROLE_DEFAULTS(role_id, admin_authority_id, enabled) SELECT id, 9, 0 FROM USER_ROLE WHERE id IN (2, 3);

INSERT INTO USER_ADMIN_AUTHORITY SELECT USER.id, ADMIN_AUTHORITY_ROLE_DEFAULTS.admin_authority_id, ADMIN_AUTHORITY_ROLE_DEFAULTS.enabled
                                 FROM USER
                                   JOIN ADMIN_AUTHORITY_ROLE_DEFAULTS ON USER.roleid = ADMIN_AUTHORITY_ROLE_DEFAULTS.role_id
    WHERE ADMIN_AUTHORITY_ROLE_DEFAULTS.admin_authority_id = 9;

INSERT INTO DATABASE_PATCH VALUES('patch_82_add_auth_edit_user',default,1);