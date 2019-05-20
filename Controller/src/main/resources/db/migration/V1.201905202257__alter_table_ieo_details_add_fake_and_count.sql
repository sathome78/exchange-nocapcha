DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_DETAILS' AND column_name = 'fake_ieo')  THEN

      ALTER TABLE IEO_DETAILS ADD COLUMN fake_ieo TINYINT(1) NULL;
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;


DELIMITER ;;
CREATE PROCEDURE Alter_Table()
  BEGIN
    IF NOT EXISTS( SELECT NULL
                   FROM INFORMATION_SCHEMA.COLUMNS
                   WHERE table_name = 'IEO_DETAILS' AND column_name = 'count_fake_transaction')  THEN

      ALTER TABLE IEO_DETAILS ADD COLUMN count_fake_transaction int(11) NULL;
    END IF;

  END;;

DELIMITER ;

CALL Alter_Table();

DROP PROCEDURE IF EXISTS Alter_Table;
