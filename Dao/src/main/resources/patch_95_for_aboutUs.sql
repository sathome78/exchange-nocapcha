CREATE TABLE IF NOT EXISTS NEWS_TYPE (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO NEWS_TYPE (`id`, `name`) VALUES
	(1, 'NEWS'),
	(2, 'MATERIALS'),
	(3, 'WEBINAR'),
	(4, 'VIDEO'),
	(5, 'EVENT'),
	(6, 'FEASTDAY'),
	(7, 'PAGE');

CREATE TABLE NEWS_EXT (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`resource` VARCHAR(100) NULL DEFAULT NULL,
	`description` VARCHAR(200) NULL DEFAULT NULL,
	`news_type_id` INT(11) NULL DEFAULT NULL,
	`calendar_date` DATE NULL DEFAULT NULL,
	`no_title_img` BIT(1) NULL DEFAULT 0,
	PRIMARY KEY (`id`),
	INDEX `FK_news_news_type` (`news_type_id`),
	CONSTRAINT `FK_news_news_type` FOREIGN KEY (`news_type_id`) REFERENCES NEWS_TYPE  (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;


CREATE TABLE NEWS_VARIANTS_EXT (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`news_id` INT(11) NOT NULL,
	`title` VARCHAR(1024) NULL DEFAULT NULL,
	`language` VARCHAR(10) NULL DEFAULT NULL,
	`brief` VARCHAR(1024) NULL DEFAULT NULL,
	`content` VARCHAR(8192) NULL DEFAULT NULL,
	`added_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	`active` BIT(1) NULL DEFAULT b'1',
	`visit_count` INT(11) NULL DEFAULT '0',
	`tags` VARCHAR(1024) NULL DEFAULT NULL,
	`sync_to_wallet_date` TIMESTAMP NULL DEFAULT NULL,
	`updated_date` TIMESTAMP NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `news_id` (`news_id`, `language`),
	CONSTRAINT `news_variants_ext_fk` FOREIGN KEY (`news_id`) REFERENCES NEWS_EXT (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO NEWS_EXT (`id`, `date`, `resource`, `description`, `news_type_id`, `calendar_date`, `no_title_img`) VALUES (1, '2017-01-31 11:08:13', 'ABOUTUS/', NULL, 7, '2017-12-07', b'1');

