CREATE TABLE IF NOT EXISTS QUBERA_RESPONSE_LOG (
  paymentId       INTEGER      NOT NULL,
  messageId       INTEGER      NOT NULL,
  accountIBAN     VARCHAR(150) NOT NULL,
  accountNumber   VARCHAR(150) NOT NULL,
  processingTime  TIMESTAMP    NOT NULL,
  received        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  state           VARCHAR(150) NOT NULL,
  currency        VARCHAR(10)  NOT NULL,
  paymentAmount   FLOAT,
  transferType    VARCHAR(150),
  rejectionReason TEXT
)
  ENGINE InnoDB;

DROP PROCEDURE IF EXISTS `Add_Constraint`;

DELIMITER $$
CREATE PROCEDURE `Add_Constraint`()
  BEGIN
    IF NOT EXISTS( SELECT `TABLE_SCHEMA`, `TABLE_NAME`
                   FROM `information_schema`.`KEY_COLUMN_USAGE`
                   WHERE `CONSTRAINT_NAME` IN ('pk_qubera_response_log'))  THEN

      ALTER TABLE `QUBERA_RESPONSE_LOG`
        ADD CONSTRAINT `pk_qubera_response_log` PRIMARY KEY (paymentId);
    END IF;

  END $$
DELIMITER ;

CALL Add_Constraint();

DROP PROCEDURE `Add_Constraint`;
