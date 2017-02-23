ALTER TABLE USER_COMMENT
	COLLATE='utf8_general_ci';

CREATE TABLE USER_COMMENT_TOPIC (
	id INT(11) NOT NULL AUTO_INCREMENT,
	topic VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO USER_COMMENT_TOPIC (id, topic) VALUES (1, 'GENERAL');
INSERT INTO USER_COMMENT_TOPIC (id, topic) VALUES (2, 'INVOICE_DECLINE');

ALTER TABLE USER_COMMENT
	ADD COLUMN topic_id INT NULL;

ALTER TABLE USER_COMMENT
	ADD CONSTRAINT FK_user_comment_user_comment_topic FOREIGN KEY (topic_id) REFERENCES USER_COMMENT_TOPIC (id);

UPDATE USER_COMMENT
SET topic_id=1;

CREATE TABLE PHRASE_TEMPLATE (
	id INT(11) NOT NULL AUTO_INCREMENT,
	template VARCHAR(100) NOT NULL,
	topic_id INT NULL,
	PRIMARY KEY (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

ALTER TABLE PHRASE_TEMPLATE
	ADD CONSTRAINT FK_phrase_template_user_comment_topic FOREIGN KEY (topic_id) REFERENCES USER_COMMENT_TOPIC (id);

INSERT INTO PHRASE_TEMPLATE (id, template, topic_id) VALUES (1, 'merchants.invoice.phrases.decline.1', 2);
INSERT INTO PHRASE_TEMPLATE (id, template, topic_id) VALUES (2, 'merchants.invoice.phrases.decline.2', 2);
INSERT INTO PHRASE_TEMPLATE (id, template, topic_id) VALUES (3, 'merchants.invoice.phrases.decline.3', 2);
