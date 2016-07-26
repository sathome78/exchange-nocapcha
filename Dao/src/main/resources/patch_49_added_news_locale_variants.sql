ALTER TABLE NEWS_VARIANTS CHANGE news_variant news_variant ENUM('RU','EN','CN', 'TH', 'HI');


INSERT INTO DATABASE_PATCH VALUES ('patch_49_added_news_locale_variants', default, 1);