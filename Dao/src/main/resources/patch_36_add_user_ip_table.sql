CREATE TABLE USER_IP (
  user_id int(40) NOT NULL,
  ip varchar(45) NOT NULL,
  confirmed tinyint(1) DEFAULT NULL COMMENT '1 if ip is confirmed',
  registration_date timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'date when user has registered from this ip',
  confirm_date timestamp NULL DEFAULT NULL COMMENT 'date when user has cofirmed this ip',
  PRIMARY KEY (user_id,ip),
  KEY user_id (user_id),
  CONSTRAINT user_ip_fk FOREIGN KEY (user_id) REFERENCES USER (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE TEMPORAL_TOKEN
  ADD COLUMN check_ip VARCHAR(45) DEFAULT NULL;

INSERT INTO TOKEN_TYPE (name) VALUES ('confirmNewIp');

INSERT INTO DATABASE_PATCH VALUES('patch_36_add_user_ip_table',default,1);