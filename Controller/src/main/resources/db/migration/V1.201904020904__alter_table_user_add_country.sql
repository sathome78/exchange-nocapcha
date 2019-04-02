DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    /* delete columns if they exist */
    IF EXISTS (SELECT *
               FROM INFORMATION_SCHEMA.COLUMNS
               WHERE table_name = 'USER' AND column_name = 'country') THEN

      ALTER TABLE USER DROP COLUMN `country`;
    END IF;

    /* add columns */
    ALTER TABLE USER ADD COLUMN country VARCHAR(255);


    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'USER' AND column_name = 'country')  THEN

      ALTER TABLE USER ADD COLUMN country VARCHAR(255);
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;