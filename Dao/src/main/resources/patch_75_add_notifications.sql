CREATE TABLE `NOTIFICATION_EVENT` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);


CREATE TABLE `NOTIFICATION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `title` varchar(45) NOT NULL,
  `message` varchar(200) NOT NULL,
  `creation_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notification_event_id` int(11) NOT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id_idx` (`user_id`),
  KEY `notification_event_id_idx` (`notification_event_id`),
  CONSTRAINT `notification_event_id` FOREIGN KEY (`notification_event_id`) REFERENCES `NOTIFICATION_EVENT` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `USER` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO NOTIFICATION_EVENT VALUES (1, 'CUSTOM', NULL);
INSERT INTO NOTIFICATION_EVENT VALUES (2, 'ADMIN', NULL);
INSERT INTO NOTIFICATION_EVENT VALUES (3, 'ACCOUNT', NULL);
INSERT INTO NOTIFICATION_EVENT VALUES (4, 'ORDER', NULL);
INSERT INTO NOTIFICATION_EVENT VALUES (5, 'IN_OUT', NULL);

INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_75_add_notifications', DEFAULT, 1);