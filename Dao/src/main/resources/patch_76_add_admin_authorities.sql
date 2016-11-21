CREATE TABLE `ADMIN_AUTHORITY` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);

INSERT INTO ADMIN_AUTHORITY VALUES(1, 'PROCESS_WITHDRAW', NULL);
INSERT INTO ADMIN_AUTHORITY VALUES(2, 'PROCESS_INVOICE', NULL);
INSERT INTO ADMIN_AUTHORITY VALUES(3, 'DELETE_ORDER', NULL);
INSERT INTO ADMIN_AUTHORITY VALUES(4, 'COMMENT_USER', NULL);
INSERT INTO ADMIN_AUTHORITY VALUES(5, 'MANAGE_SESSIONS', NULL);
INSERT INTO ADMIN_AUTHORITY VALUES(6, 'SET_CURRENCY_LIMIT', NULL);
INSERT INTO ADMIN_AUTHORITY VALUES(7, 'MANAGE_ACCESS', NULL);

CREATE TABLE `ADMIN_AUTHORITY_ROLE_DEFAULTS` (
  `role_id` int(11) NOT NULL,
  `admin_authority_id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`role_id`,`admin_authority_id`),
  KEY `fk_auth_admin_auth_id_idx` (`admin_authority_id`),
  CONSTRAINT `fk_auth_admin_auth_id` FOREIGN KEY (`admin_authority_id`) REFERENCES `ADMIN_AUTHORITY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_auth_user_role_id` FOREIGN KEY (`role_id`) REFERENCES `USER_ROLE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO ADMIN_AUTHORITY_ROLE_DEFAULTS SELECT USER_ROLE.id, ADMIN_AUTHORITY.id, 0 FROM USER_ROLE
  JOIN ADMIN_AUTHORITY WHERE USER_ROLE.id IN (1,2,3);

UPDATE ADMIN_AUTHORITY_ROLE_DEFAULTS SET enabled = 1 WHERE role_id = 1
                                                           OR (role_id = 2 AND admin_authority_id IN(1,2,3,6))
                                                           OR (role_id = 3 AND admin_authority_id IN(1,2,3,4,6));

CREATE TABLE `USER_ADMIN_AUTHORITY` (
  `user_id` int(11) NOT NULL,
  `admin_authority_id` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`user_id`,`admin_authority_id`),
  KEY `fk_admin_authority_id_idx` (`admin_authority_id`),
  CONSTRAINT `fk_admin_authority_id` FOREIGN KEY (`admin_authority_id`) REFERENCES `ADMIN_AUTHORITY` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_id_auth` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO USER_ADMIN_AUTHORITY SELECT USER.id, ADMIN_AUTHORITY_ROLE_DEFAULTS.admin_authority_id, ADMIN_AUTHORITY_ROLE_DEFAULTS.enabled
                                 FROM USER
                                   JOIN ADMIN_AUTHORITY_ROLE_DEFAULTS ON USER.roleid = ADMIN_AUTHORITY_ROLE_DEFAULTS.role_id;
