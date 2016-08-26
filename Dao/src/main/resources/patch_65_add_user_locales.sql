ALTER TABLE USER
  CHANGE COLUMN `preferred_lang` `preferred_lang` ENUM('en', 'ru', 'cn', 'in', 'ar') NULL DEFAULT 'en' ;

INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_65_add_user_locales', DEFAULT, 1);
