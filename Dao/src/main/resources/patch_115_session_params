DROP TABLE IF EXISTS SESSION_PARAMS;
DROP TABLE IF EXISTS SESSION_LIFE_TIME_TYPE;

CREATE TABLE SESSION_LIFE_TIME_TYPE (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `active` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`id`),
  UNIQUE (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE SESSION_PARAMS (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `session_time_minutes` int(11) NOT NULL,
  `session_life_type_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`user_id`),
  KEY `FK_session_params_user_id` (`user_id`),
  KEY `FK_session_params_session_life_type_id` (`session_life_type_id`),
  CONSTRAINT `FK_session_params_user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_session_params_session_life_type_id` FOREIGN KEY (`session_life_type_id`) REFERENCES `SESSION_LIFE_TIME_TYPE` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO SESSION_LIFE_TIME_TYPE (id, name) VALUES (1, 'FIXED_LIFETIME'), (2, 'INACTIVE_COUNT_LIFETIME');

COMMIT;