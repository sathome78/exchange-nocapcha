ALTER TABLE USER
  ADD COLUMN `avatar_path` VARCHAR(64) NULL AFTER `preferred_lang`,
  ADD UNIQUE INDEX `avatar_path_UNIQUE` (`avatar_path` ASC);

INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_69_add_avatar', DEFAULT, 1);
