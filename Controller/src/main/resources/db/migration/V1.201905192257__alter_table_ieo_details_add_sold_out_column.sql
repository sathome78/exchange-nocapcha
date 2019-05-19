DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_DETAILS' AND column_name = 'sold_out_at')  THEN

      ALTER TABLE IEO_DETAILS ADD COLUMN sold_out_at timestamp NULL;
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
