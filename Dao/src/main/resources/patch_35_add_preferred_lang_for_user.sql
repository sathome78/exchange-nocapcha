ALTER TABLE USER
  ADD COLUMN preferred_lang enum('en','ru') DEFAULT 'en';

INSERT INTO DATABASE_PATCH VALUES('patch_35_add_preferred_lang_for_user',default,1);