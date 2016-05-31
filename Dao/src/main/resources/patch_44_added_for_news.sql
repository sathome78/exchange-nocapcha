DROP TABLE IF EXISTS NEWS_VARIANTS;
DROP TABLE IF EXISTS NEWS;

CREATE TABLE NEWS (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата, которой датируется новость',
  `resource` varchar(100) DEFAULT NULL COMMENT 'Относительный путь размещения файлов (html, css, img, и т.п.) контента новости. ',
  `description` varchar(200) DEFAULT NULL COMMENT 'Описание, используемое для внутренних целей (не для публикации)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;

CREATE TABLE NEWS_VARIANTS (
  `news_id` int(11) NOT NULL,
  `title` varchar(100) DEFAULT NULL COMMENT 'Заголовок новости на языке, соответствующем языковому варианту новости',
  `news_variant` enum('RU','EN') NOT NULL COMMENT 'Язык новости - вариант новости',
  `brief` varchar(250) DEFAULT NULL COMMENT 'Краткое содержание новости на языке, соответствующем языковому варианту новости',
  `added_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата добавления языкового варианта. Может отличаться от даты самой новости',
  `active` tinyint(4) DEFAULT '1' COMMENT 'признак активности варианта новости. Неактивный вариант не будет отображаться',
  PRIMARY KEY (`news_id`,`news_variant`),
  KEY `news_id` (`news_id`),
  CONSTRAINT `news_variants_fk` FOREIGN KEY (`news_id`) REFERENCES NEWS (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO DATABASE_PATCH VALUES('patch_44_added_for_news',default,1);