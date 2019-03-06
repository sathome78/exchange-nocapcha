DROP PROCEDURE IF EXISTS `Alter_Table`;

DELIMITER $$
CREATE PROCEDURE `Alter_Table`()
BEGIN

    IF EXISTS( SELECT NULL
            FROM INFORMATION_SCHEMA.COLUMNS
           WHERE table_name = 'USER'
             AND table_schema = 'birzha'
             AND column_name = 'kyc_reference')  THEN

      ALTER TABLE `USER` DROP COLUMN kyc_reference;
    END IF;

    IF NOT EXISTS( SELECT NULL
            FROM INFORMATION_SCHEMA.COLUMNS
           WHERE table_name = 'USER'
             AND table_schema = 'birzha'
             AND column_name = 'kyc_status')  THEN

      ALTER TABLE USER ADD COLUMN kyc_status VARCHAR(55) NOT NULL DEFAULT 'none';
    END IF;

    IF NOT EXISTS( SELECT NULL
            FROM INFORMATION_SCHEMA.COLUMNS
           WHERE table_name = 'USER'
             AND table_schema = 'birzha'
             AND column_name = 'kyc_reference')  THEN

      ALTER TABLE USER ADD COLUMN kyc_reference VARCHAR(55);
    END IF;

END $$
DELIMITER ;

DROP PROCEDURE `Alter_Table`;