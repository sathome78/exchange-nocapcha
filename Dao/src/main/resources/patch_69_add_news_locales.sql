ALTER TABLE NEWS_VARIANTS
  CHANGE COLUMN `news_variant` `news_variant` ENUM('RU', 'EN', 'CN', 'IN', 'AR') NOT NULL DEFAULT 'RU' ;
INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_69_add_news_locales', DEFAULT, 1);