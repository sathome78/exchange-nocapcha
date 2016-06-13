DROP PROCEDURE IF EXISTS GET_DATA_FOR_CANDLE;
DELIMITER ;;
CREATE PROCEDURE GET_DATA_FOR_CANDLE(
  IN end_point        TIMESTAMP,
  IN interval_value   INTEGER(11),
  IN interval_type    VARCHAR(10),
  IN currency_pair_id INTEGER(11)
)
NOT DETERMINISTIC
READS SQL DATA
  SQL SECURITY DEFINER
  COMMENT ''
  BEGIN
    DECLARE start_point TIMESTAMP;
    DECLARE step_value INT;
    DECLARE step_type VARCHAR(10);
    DECLARE currentPoint TIMESTAMP;
    DECLARE predPoint TIMESTAMP;
/**/
    DECLARE status_id INT DEFAULT 3;
/**/
    DECLARE low_rate DOUBLE(40, 9);
    DECLARE high_rate DOUBLE(40, 9);
    DECLARE open_rate DOUBLE(40, 9);
    DECLARE close_rate DOUBLE(40, 9);
    DECLARE base_volume DOUBLE(40, 9);
/**/
    DECLARE first_rate DOUBLE(40, 9);
/**/
/*FOR TEST ... */
/*SELECT STR_TO_DATE('2016-05-04 10:00:00', '%Y-%m-%d %h:%i:%s') INTO end_point;
/*... FOR TEST*/
    SET end_point = IFNULL(end_point, NOW());
/**/
    IF (interval_type = 'HOUR')
    THEN
      SELECT DATE_SUB(end_point, INTERVAL interval_value HOUR)
      INTO start_point;
      IF (interval_value = 12)
      THEN
        SET step_value = 20;
        SET step_type = 'MINUTE';
      ELSEIF (interval_value = 24)
        THEN
          SET step_value = 40;
          SET step_type = 'MINUTE';
      ELSE
        SET step_value = ROUND(interval_value * 60 / 50);
        SET step_type = 'MINUTE';
      END IF;
    ELSEIF (interval_type = 'DAY')
      THEN
        SELECT DATE_SUB(end_point, INTERVAL interval_value DAY)
        INTO start_point;
        IF (interval_value = 7)
        THEN
          SET step_value = 4;
          SET step_type = 'HOUR';
        ELSE
          SET step_value = ROUND(interval_value * 24 / 50);
          SET step_type = 'HOUR';
        END IF;
    ELSEIF (interval_type = 'MONTH')
      THEN
        SELECT DATE_SUB(end_point, INTERVAL interval_value MONTH)
        INTO start_point;
        IF (interval_value = 1)
        THEN
          SET step_value = 18;
          SET step_type = 'HOUR';
        ELSEIF (interval_value = 6)
          THEN
            SET step_value = 6;
            SET step_type = 'DAY';
        ELSE
          SET step_value = ROUND(interval_value * 30 / 50);
          SET step_type = 'DAY';
        END IF;
    ELSEIF (interval_type = 'YEAR')
      THEN
        SELECT DATE_SUB(end_point, INTERVAL interval_value YEAR)
        INTO start_point;
        SET step_value = ROUND(interval_value * 360 / 72);
        SET step_type = 'DAY';
    END IF;
/**/
    DROP TABLE IF EXISTS CANDLE_TMP_TBL;
    CREATE TEMPORARY TABLE CANDLE_TMP_TBL (
      pred_point    TIMESTAMP,
      current_point TIMESTAMP,
      low_rate      DOUBLE(40, 9),
      high_rate     DOUBLE(40, 9),
      open_rate     DOUBLE(40, 9),
      close_rate    DOUBLE(40, 9),
      base_volume   DOUBLE(40, 9)
    );
/**/
    SET currentPoint = start_point;

    WHILE (currentPoint < end_point) DO
      SET predPoint = currentPoint;
/**/
      IF (step_type = 'MINUTE')
      THEN
        SELECT DATE_ADD(currentPoint, INTERVAL step_value MINUTE)
        INTO currentPoint;
      ELSEIF (step_type = 'HOUR')
        THEN
          SELECT DATE_ADD(currentPoint, INTERVAL step_value HOUR)
          INTO currentPoint;
      ELSEIF (step_type = 'DAY')
        THEN
          SELECT DATE_ADD(currentPoint, INTERVAL step_value DAY)
          INTO currentPoint;
      ELSEIF (step_type = 'MONTH')
        THEN
          SELECT DATE_ADD(currentPoint, INTERVAL step_value MONTH)
          INTO currentPoint;
      ELSE
        SET currentPoint = end_point;
      END IF;
      IF (currentPoint > end_point)
      THEN
        SET currentPoint = end_point;
      END IF;
/**/
      SET low_rate = NULL;
      SET high_rate = NULL;
      SET open_rate = NULL;
      SET first_rate = NULL;
      SET base_volume = 0;
/**/
      SELECT EXORDERS.exrate AS pred_period_last_exrate
      FROM EXORDERS
      WHERE (EXORDERS.date_acception <= predPoint) AND
            (EXORDERS.currency_pair_id = currency_pair_id) AND
            (EXORDERS.status_id = status_id)
      ORDER BY EXORDERS.date_acception DESC, EXORDERS.id DESC
      LIMIT 1
      INTO open_rate;
/**/
      SELECT
        AGRIGATE.low      AS low_rate,
        AGRIGATE.high     AS high_rate,
        LASTORDER.exrate  AS close_rate,
        FIRSTORDER.exrate AS first_rate,
        AGRIGATE.baseVolume
      FROM
        (SELECT
           EXORDERS.currency_pair_id,
           EXORDERS.status_id,
           MIN(EXORDERS.date_acception) AS first_date_acception,
           MAX(EXORDERS.date_acception) AS last_date_acception,
           MIN(EXORDERS.exrate)         AS low,
           MAX(EXORDERS.exrate)         AS high,
           SUM(EXORDERS.amount_base)    AS baseVolume,
           SUM(EXORDERS.amount_convert) AS convertVolume
         FROM EXORDERS
         WHERE
           EXORDERS.currency_pair_id = currency_pair_id AND
           EXORDERS.status_id = status_id AND
           EXORDERS.date_acception > predPoint AND
           EXORDERS.date_acception <= currentPoint
         GROUP BY EXORDERS.currency_pair_id, EXORDERS.status_id)
        AGRIGATE

        JOIN EXORDERS LASTORDER ON (LASTORDER.date_acception = AGRIGATE.last_date_acception) AND
                                   (LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND
                                   (LASTORDER.status_id = AGRIGATE.status_id)

        JOIN EXORDERS FIRSTORDER ON (FIRSTORDER.date_acception = AGRIGATE.first_date_acception) AND
                                    (FIRSTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND
                                    (FIRSTORDER.status_id = AGRIGATE.status_id)

      ORDER BY LASTORDER.id DESC, FIRSTORDER.id ASC
      LIMIT 1
      INTO low_rate, high_rate, close_rate, first_rate, base_volume;
/**/
/*open_rate = close_rate from pred period (not first deal in current period)*/
/*if there are no deals in pred periods, open_rate = first deal in current period*/
      SET open_rate = IFNULL(open_rate, first_rate);
/*if there are no deals in current period open_rate = close_rate from last period with deal*/
      SET open_rate = IFNULL(open_rate, close_rate);
      SET open_rate = IFNULL(open_rate, 0);
      SET close_rate = IFNULL(close_rate, open_rate);
      SET low_rate = IFNULL(low_rate, open_rate);
      SET high_rate = IFNULL(high_rate, close_rate);
/**/
      INSERT INTO CANDLE_TMP_TBL
      VALUES (predPoint, currentPoint, low_rate, high_rate, open_rate, close_rate, base_volume);
    END WHILE;
/**/
    SELECT *
    FROM CANDLE_TMP_TBL;
    DROP TABLE IF EXISTS CANDLE_TMP_TBL;
  END;;

DELIMITER ;
INSERT INTO DATABASE_PATCH VALUES ('patch_47_update_GET_DATA_FOR_CANDLE', default, 1);