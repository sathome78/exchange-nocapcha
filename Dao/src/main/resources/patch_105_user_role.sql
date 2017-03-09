CREATE TABLE USER_ROLE_BUSINESS_FEATURE (
	id INT(11) NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NULL DEFAULT NULL,
	PRIMARY KEY (id)
)
ENGINE=InnoDB;

INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (1, 'ADMIN');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (2, 'USER');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (3, 'EXCHANGE');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (4, 'VIP_USER');
INSERT INTO USER_ROLE_BUSINESS_FEATURE (id, name) VALUES (5, 'TRADER');

CREATE TABLE USER_ROLE_GROUP_FEATURE (
	id INT(11) NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NULL DEFAULT NULL,
	PRIMARY KEY (id)
)
ENGINE=InnoDB;

INSERT INTO USER_ROLE_GROUP_FEATURE (id, name) VALUES (1, 'ADMINS');
INSERT INTO USER_ROLE_GROUP_FEATURE (id, name) VALUES (2, 'USERS');

ALTER TABLE USER_ROLE
	ADD COLUMN user_role_business_feature_id INT NULL;

ALTER TABLE USER_ROLE
	ADD COLUMN user_role_group_feature_id INT(11) NULL DEFAULT NULL ;

ALTER TABLE USER_ROLE
	ADD CONSTRAINT FK_user_role_user_role_business_feature FOREIGN KEY (user_role_business_feature_id) REFERENCES USER_ROLE_BUSINESS_FEATURE (id),
	ADD CONSTRAINT FK_user_role_user_role_group_feature FOREIGN KEY (user_role_group_feature_id) REFERENCES USER_ROLE_GROUP_FEATURE (id);

UPDATE USER_ROLE SET user_role_business_feature_id='1' WHERE  id IN (1,2,3,9);
UPDATE USER_ROLE SET user_role_business_feature_id='2' WHERE  id IN (4);
UPDATE USER_ROLE SET user_role_business_feature_id='3' WHERE  id IN (6);
UPDATE USER_ROLE SET user_role_business_feature_id='4' WHERE  id IN (7);
UPDATE USER_ROLE SET user_role_business_feature_id='5' WHERE  id IN (8);
UPDATE USER_ROLE SET user_role_group_feature_id='1' WHERE  id IN (1,2,3,9);
UPDATE USER_ROLE SET user_role_group_feature_id='2' WHERE  id NOT IN (1,2,3,9);