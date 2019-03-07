DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER $$
CREATE PROCEDURE `Alter_Table`()
  BEGIN

    IF EXISTS( SELECT NULL
               FROM INFORMATION_SCHEMA.COLUMNS
               WHERE table_name = 'CURRENCY'
                     AND table_schema = 'birzha'
                     AND column_name = 'scale')  THEN

      ALTER TABLE `CURRENCY` DROP COLUMN scale;
    END IF;

    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'CURRENCY'
                         AND table_schema = 'birzha'
                         AND column_name = 'scale')  THEN

      ALTER TABLE `CURRENCY` ADD COLUMN scale INT(11) NULL;
    END IF;

  END $$
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE `Alter_Table`;

UPDATE CURRENCY SET scale = 2 WHERE id IN (1, 2, 3, 7, 8, 10, 11, 12, 13, 17, 18, 20);
UPDATE CURRENCY SET scale = 8 WHERE id NOT IN (1, 2, 3, 7, 8, 10, 11, 12, 13, 17, 18, 20);