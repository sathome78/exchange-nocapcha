DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER $$
CREATE PROCEDURE `Alter_Table`()
  BEGIN

    IF EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'CURRENCY_PAIR'
                         AND table_schema = 'birzha'
                         AND column_name = 'scale')  THEN

      ALTER TABLE `CURRENCY_PAIR` DROP COLUMN scale;
    END IF;

    IF NOT EXISTS( SELECT NULL
               FROM INFORMATION_SCHEMA.COLUMNS
               WHERE table_name = 'CURRENCY_PAIR'
                     AND table_schema = 'birzha'
                     AND column_name = 'scale')  THEN

      ALTER TABLE `CURRENCY_PAIR` ADD COLUMN scale INT(11) NULL;
    END IF;

  END $$
DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE `Alter_Table`;

UPDATE CURRENCY_PAIR SET scale = 2 WHERE market = 'FIAT';
UPDATE CURRENCY_PAIR SET scale = 2 WHERE market = 'USD';
UPDATE CURRENCY_PAIR SET scale = 8 WHERE market = 'BTC';
UPDATE CURRENCY_PAIR SET scale = 8 WHERE market = 'ETH';