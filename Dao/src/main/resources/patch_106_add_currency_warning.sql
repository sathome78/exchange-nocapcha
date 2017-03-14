
INSERT INTO USER_COMMENT_TOPIC (id, topic) VALUES
  (3, 'CURRENCY_WARNING');

INSERT INTO PHRASE_TEMPLATE (template, topic_id) VALUES
  ('input.warning.EDR', 3),
  ('input.warning.BTC', 3);

CREATE TABLE `CURRENCY_WARNING` (
  `currency_id` int(11) NOT NULL,
  `phrase_template_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`currency_id`),
  KEY `currency_warning___fk_phrase_id` (`phrase_template_id`),
  CONSTRAINT `currency_warning___fk_curr_id` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `currency_warning___fk_phrase_id` FOREIGN KEY (`phrase_template_id`) REFERENCES `PHRASE_TEMPLATE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO CURRENCY_WARNING VALUES (4, 5);
INSERT INTO CURRENCY_WARNING VALUES (9, 4);