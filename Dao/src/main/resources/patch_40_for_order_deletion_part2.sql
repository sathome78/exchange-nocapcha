
DROP PROCEDURE IF EXISTS DELETE_ORDER;

DELIMITER ;;

CREATE PROCEDURE DELETE_ORDER (
  IN deleted_order_id INTEGER(11)
)
NOT DETERMINISTIC
MODIFIES SQL DATA
  SQL SECURITY INVOKER
  COMMENT 'CANCEL ORDER'
  BEGIN
/*
  return (intepretation):
  -1: error
  0: no rows were obtained for the deleted_order_id: order has not status 2 or 3
  1: exorder were not be accepted - there were no associated transaction
  n: number of processed rows (including exorders and transaction)
  */
    DECLARE processedRows INT DEFAULT 0;

    DECLARE order_id INT;
    DECLARE order_status_id INT;
    DECLARE order_creator_reserved_amount double(40,9);
    DECLARE order_creator_reserved_wallet_id INT;
    DECLARE transaction_id INT;
    DECLARE transaction_type_name VARCHAR(10);
    DECLARE transaction_amount double(40,9);
    DECLARE user_wallet_id INT;
    DECLARE company_wallet_id INT;
    DECLARE company_commission double(40,9);

    DECLARE order_detailed_table CURSOR
    FOR SELECT
          EXORDERS.id AS order_id,
          EXORDERS.status_id AS order_status_id,
          IF (upper(ORDER_OPERATION.name)='SELL', EXORDERS.amount_base, EXORDERS.amount_convert+EXORDERS.commission_fixed_amount) AS order_creator_reserved_amount,
          ORDER_CREATOR_RESERVED_WALLET.id AS order_creator_reserved_wallet_id,
          TRANSACTION.id AS transaction_id,
          TRANSACTION_OPERATION.name as transaction_type_name,
          TRANSACTION.amount as transaction_amount,
          USER_WALLET.id as user_wallet_id,
          COMPANY_WALLET.id as company_wallet_id,
          TRANSACTION.commission_amount AS company_commission
        /**/
        FROM EXORDERS
          JOIN OPERATION_TYPE AS ORDER_OPERATION ON (ORDER_OPERATION.id = EXORDERS.operation_type_id)
          JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id)
          JOIN WALLET ORDER_CREATOR_RESERVED_WALLET ON
                                                      (ORDER_CREATOR_RESERVED_WALLET.user_id=EXORDERS.user_id) AND
                                                      (
                                                        (upper(ORDER_OPERATION.name)='BUY' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency2_id)
                                                        OR
                                                        (upper(ORDER_OPERATION.name)='SELL' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency1_id)
                                                      )
/**/
          LEFT JOIN TRANSACTION ON (TRANSACTION.order_id = EXORDERS.id)
          LEFT JOIN OPERATION_TYPE TRANSACTION_OPERATION ON (TRANSACTION_OPERATION.id = TRANSACTION.operation_type_id)
          LEFT JOIN WALLET USER_WALLET ON (USER_WALLET.id = TRANSACTION.user_wallet_id)
          LEFT JOIN COMPANY_WALLET ON (COMPANY_WALLET.currency_id = TRANSACTION.company_wallet_id) and (TRANSACTION.commission_amount <> 0)
/**/
      WHERE EXORDERS.id=deleted_order_id;

    BEGIN
      DECLARE eof boolean DEFAULT false;
      DECLARE EXIT HANDLER FOR SQLEXCEPTION
      BEGIN
        SELECT -1;
        ROLLBACK;
      END;
      DECLARE CONTINUE HANDLER FOR NOT FOUND SET eof=TRUE;
      SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
      START TRANSACTION;
      OPEN order_detailed_table;
      FETCH order_detailed_table INTO
        order_id,
        order_status_id,
        order_creator_reserved_amount,
        order_creator_reserved_wallet_id,
        transaction_id,
        transaction_type_name,
        transaction_amount,
        user_wallet_id,
        company_wallet_id,
        company_commission;
      IF (order_status_id in (2, 3)) THEN
        UPDATE EXORDERS
        SET status_id = 5 /*deleted by admin*/
        WHERE id = order_id;
        SET processedRows = processedRows + 1;
        WHILE (eof = false) DO
          IF (order_status_id = 3) THEN
/*accepted order*/
            UPDATE COMPANY_WALLET
            SET commission_balance = commission_balance - company_commission
            WHERE id = company_wallet_id;
            UPDATE WALLET
            SET active_balance = active_balance + IF (transaction_type_name = 'INPUT', -1, 1)*transaction_amount
            WHERE id = user_wallet_id;
            UPDATE TRANSACTION
            SET status_id = 2 /*deleted by admin*/
            WHERE id = transaction_id;
            SET processedRows = processedRows + 1;
          ELSEIF (order_status_id = 2) THEN
/*open order*/
            UPDATE WALLET
            SET active_balance = active_balance + order_creator_reserved_amount,
              reserved_balance = reserved_balance - order_creator_reserved_amount
            WHERE id = order_creator_reserved_wallet_id;
          END IF;
          FETCH order_detailed_table INTO
            order_id,
            order_status_id,
            order_creator_reserved_amount,
            order_creator_reserved_wallet_id,
            transaction_id,
            transaction_type_name,
            transaction_amount,
            user_wallet_id,
            company_wallet_id,
            company_commission;
        END WHILE;
      END IF;
      COMMIT;
      SELECT processedRows;
    END;

    CLOSE order_detailed_table;

  END;;

DELIMITER ;

INSERT INTO DATABASE_PATCH VALUES('patch_40_for_order_deletion_part2',default,1);