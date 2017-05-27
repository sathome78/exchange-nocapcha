ALTER TABLE USER
	ADD COLUMN tmp_poll_passed TINYINT NOT NULL AFTER avatar_path;

CREATE TABLE SURVEY (
	id INT(11) NOT NULL AUTO_INCREMENT,
	token VARCHAR(100) NOT NULL,
	active TINYINT(4) NOT NULL DEFAULT '1',
	json VARCHAR(21000) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX token (token)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE SURVEY_ITEM (
	id INT(11) NOT NULL AUTO_INCREMENT,
	survey_id INT(11) NOT NULL,
	lang VARCHAR(5) NOT NULL,
	name VARCHAR(50) NOT NULL,
	title VARCHAR(300) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX survey_id_lang_name (survey_id, lang, name),
	CONSTRAINT FK_survey_item_survey FOREIGN KEY (survey_id) REFERENCES SURVEY (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE SURVEY_LANG_PARAM (
	id INT(11) NOT NULL AUTO_INCREMENT,
	survey_id INT(11) NOT NULL,
	lang VARCHAR(5) NOT NULL,
	description VARCHAR(1024) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX survey_lang_param_id_lang_name (survey_id, lang),
	CONSTRAINT FK_survey_item_survey_lang_param FOREIGN KEY (survey_id) REFERENCES SURVEY (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO SURVEY VALUES (1, '77fe16c6-516b-49ec-b362-44b4ca159136', 1, '{\r\n	"locale": "ru",\r\n	"pages": [\r\n		{\r\n			"elements": [\r\n				{\r\n					"type": "rating",\r\n					"isRequired": true,\r\n					"name": "question1",\r\n					"title": {\r\n						"ru": "Низкий объем торгов"\r\n					}\r\n				},\r\n				{\r\n					"type": "rating",\r\n					"isRequired": true,\r\n					"name": "question2",\r\n					"title": {\r\n						"ru": "Не удобный ввод/вывод монет и валют"\r\n					}\r\n				},\r\n				{\r\n					"type": "rating",\r\n					"isRequired": true,\r\n					"name": "question3",\r\n					"title": {\r\n						"ru": "Высокие комиссии торгов и ввода/вывода"\r\n					}\r\n				},\r\n				{\r\n					"type": "rating",\r\n					"isRequired": true,\r\n					"name": "question4",\r\n					"title": {\r\n						"ru": "Не удобный интерфейс для провдения торгов"\r\n					}\r\n				},\r\n				{\r\n					"type": "rating",\r\n					"isRequired": true,\r\n					"name": "question5",\r\n					"title": {\r\n						"ru": "Ограниченный набор криптовалют"\r\n					}\r\n				},\r\n				{\r\n					"type": "comment",\r\n					"name": "question6",					\r\n					"rows": "5",\r\n					"title": {\r\n						"ru": "Вижу другие проблемы"\r\n					}\r\n				}\r\n			],\r\n			"name": "page1"\r\n		}\r\n	]\r\n}');

INSERT INTO SURVEY_ITEM VALUES (1, 1, 'ru', 'question1', 'Низкий объем торгов');
INSERT INTO SURVEY_ITEM VALUES (2, 1, 'ru', 'question2', 'Неудобный ввод/вывод монет и валют');
INSERT INTO SURVEY_ITEM VALUES (3, 1, 'ru', 'question3', 'Высокие комиссии торгов и ввода/вывода');
INSERT INTO SURVEY_ITEM VALUES (9, 1, 'ru', 'question4', 'Неудобный интерфейс для проведения торгов');
INSERT INTO SURVEY_ITEM VALUES (10, 1, 'ru', 'question5', 'Ограниченный набор криптовалют');
INSERT INTO SURVEY_ITEM VALUES (11, 1, 'ru', 'question6', 'Укажите другие проблемы или любой комментарий');
INSERT INTO SURVEY_ITEM VALUES (12, 1, 'en', 'question1', 'Low trading volume');
INSERT INTO SURVEY_ITEM VALUES (13, 1, 'en', 'question2', 'Inconvenient input/output of coins and currencies');
INSERT INTO SURVEY_ITEM VALUES (14, 1, 'en', 'question3', 'High commissions for the trades and input/output');
INSERT INTO SURVEY_ITEM VALUES (15, 1, 'en', 'question4', 'Inconvenient interface for trading');
INSERT INTO SURVEY_ITEM VALUES (16, 1, 'en', 'question5', 'Limited set of crypto-currencies');
INSERT INTO SURVEY_ITEM VALUES (17, 1, 'en', 'question6', 'Specify other problems or any comment');


INSERT INTO SURVEY_LANG_PARAM VALUES (1, 1, 'ru', '<b>Ваше мнение очень важно для нас !</b></br>С целью улучшения качества работы Биржи просим Вас ответить на несколько вопросов.\r\n<br>Это займет у Вас всего пару минут.\r\n</br>Пожалуйста, оцените их, поставив оценки для нескольких утверждений по принципу:\r\n</br>"1" - для меня это не проблема\r\n</br>"5" - то, что меня заставит покинуть Биржу');
INSERT INTO SURVEY_LANG_PARAM VALUES (2, 1, 'en', '<b>Your feedback is very important to us!</b></br>In order to improve the quality of the work of the Exchange, we ask you to answer a few questions.\r\n</br>It only takes you a couple of minutes.\r\n</br>Please rate them by putting estimates for several statements on the principle:\r\n</br>"1" - it is not a problem for me\r\n</br>"5" - that can force me to leave the Exchange');

