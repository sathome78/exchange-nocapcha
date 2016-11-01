ALTER TABLE NEWS_VARIANTS
  CHANGE COLUMN `news_variant` `news_variant` ENUM('ru', 'en', 'cn', 'in', 'ar') NOT NULL DEFAULT 'ru' ;
INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_72_add_news_locales', DEFAULT, 1);