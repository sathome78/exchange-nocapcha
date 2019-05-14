DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    /* delete columns if they exist */
    IF EXISTS (SELECT *
               FROM INFORMATION_SCHEMA.COLUMNS
               WHERE table_name = 'IEO_CLAIM' AND column_name = 'uuid') THEN

      ALTER TABLE IEO_CLAIM DROP COLUMN `uuid`;
    END IF;

    /* add columns */
    ALTER TABLE IEO_CLAIM ADD COLUMN uuid VARCHAR(64) NOT NULL;


    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_CLAIM' AND column_name = 'uuid')  THEN

      ALTER TABLE IEO_CLAIM ADD COLUMN uuid VARCHAR(64) NOT NULL;
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
