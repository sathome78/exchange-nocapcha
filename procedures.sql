CREATE TRIGGER `TRANSACTION_BEFORE_UPD_TR`
BEFORE UPDATE ON `TRANSACTION`
FOR EACH ROW
  BEGIN
    IF (NEW.status_id <> OLD.status_id) THEN
      SET new.status_modification_date = CURRENT_TIMESTAMP;
    END IF;
  END;

CREATE TRIGGER `EXORDERS_BEFORE_UPD_TR`
BEFORE UPDATE ON `EXORDERS`
FOR EACH ROW
  BEGIN
    IF (NEW.status_id <> OLD.status_id) THEN
      SET new.status_modification_date = CURRENT_TIMESTAMP;
    END IF;
  END

CREATE TRIGGER `STOP_ORDERS_BEFORE_UPD_TR`
BEFORE UPDATE ON `STOP_ORDERS`
FOR EACH ROW
  BEGIN
    IF (NEW.status_id <> OLD.status_id) THEN
      SET new.date_modification = CURRENT_TIMESTAMP;
    END IF;
  END

CREATE PROCEDURE `GET_DATA_FOR_CANDLE`(IN `end_point`     TIMESTAMP, IN `interval_value` INT(11),
                                       IN `interval_type` VARCHAR(10), IN `currency_pair_id` INT(11))
  BEGIN
    DECLARE start_point DATETIME;
    DECLARE step_value INT;
    DECLARE step_type VARCHAR(10);
    DECLARE currentPoint DATETIME;
    DECLARE predPoint DATETIME;

    DECLARE status_id INT DEFAULT 3;

    DECLARE low_rate DOUBLE(40, 9);
    DECLARE high_rate DOUBLE(40, 9);
    DECLARE open_rate DOUBLE(40, 9);
    DECLARE close_rate DOUBLE(40, 9);
    DECLARE base_volume DOUBLE(40, 9);

    DECLARE first_rate DOUBLE(40, 9);



    SET end_point = IFNULL(end_point, NOW());

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

    DROP TABLE IF EXISTS CANDLE_TMP_TBL;
    CREATE TEMPORARY TABLE CANDLE_TMP_TBL (
      pred_point    DATETIME DEFAULT NOW(),
      current_point DATETIME DEFAULT NOW(),
      low_rate      DOUBLE(40, 9),
      high_rate     DOUBLE(40, 9),
      open_rate     DOUBLE(40, 9),
      close_rate    DOUBLE(40, 9),
      base_volume   DOUBLE(40, 9)
    );

    SET currentPoint = start_point;

    WHILE (currentPoint < end_point) DO
      SET predPoint = currentPoint;

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

      SET low_rate = NULL;
      SET high_rate = NULL;
      SET open_rate = NULL;
      SET first_rate = NULL;
      SET base_volume = 0;

      SELECT EXORDERS.exrate AS pred_period_last_exrate
      FROM EXORDERS
      WHERE (EXORDERS.date_acception <= predPoint) AND
            (EXORDERS.currency_pair_id = currency_pair_id) AND
            (EXORDERS.status_id = status_id)
      ORDER BY EXORDERS.date_acception DESC, EXORDERS.id DESC
      LIMIT 1
      INTO open_rate;

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



      SET open_rate = IFNULL(open_rate, first_rate);

      SET open_rate = IFNULL(open_rate, close_rate);
      SET open_rate = IFNULL(open_rate, 0);
      SET close_rate = IFNULL(close_rate, open_rate);
      SET low_rate = IFNULL(low_rate, open_rate);
      SET high_rate = IFNULL(high_rate, close_rate);

      INSERT INTO CANDLE_TMP_TBL
      VALUES (predPoint, currentPoint, low_rate, high_rate, open_rate, close_rate, base_volume);
    END WHILE;

    SELECT *
    FROM CANDLE_TMP_TBL;
    DROP TABLE IF EXISTS CANDLE_TMP_TBL;
  END

CREATE PROCEDURE `GET_COINMARKETCAP_STATISTICS`(IN `currency_pair` VARCHAR(45))
  BEGIN
    DECLARE currency_pair_name VARCHAR(45);
    DECLARE currency_pair_id INT;
    DECLARE status_id INT;
    DECLARE first_date_acception TIMESTAMP;
    DECLARE last_date_acception TIMESTAMP;
    DECLARE predPoint TIMESTAMP;
    DECLARE baseVolume DOUBLE(40, 9);
    DECLARE first DOUBLE(40, 9);
    DECLARE last DOUBLE(40, 9);
    DECLARE lowestAsk DOUBLE(40, 9);
    DECLARE highestBid DOUBLE(40, 9);
    DECLARE high24hr DOUBLE(40, 9);
    DECLARE low24hr DOUBLE(40, 9);
    DECLARE quoteVolume DOUBLE(40, 9) DEFAULT 0;
    DECLARE isFrozen INT DEFAULT 0;

    DECLARE coinmarketcap_statistics VARCHAR(40);

    DECLARE AGRIGATE CURSOR
    FOR SELECT
          CP.ticker_name,
          EO.currency_pair_id,
          EO.status_id,
          MIN(EO.date_acception) AS first_date_acception,
          MAX(EO.date_acception) AS last_date_acception,
          SUM(EO.amount_base)    AS baseVolume,
          (
            SELECT FIRSTORDER.exrate
            FROM EXORDERS FIRSTORDER
            WHERE
              (FIRSTORDER.date_acception = MIN(EO.date_acception)) AND
              (FIRSTORDER.currency_pair_id = EO.currency_pair_id) AND
              (FIRSTORDER.status_id = EO.status_id)
            ORDER BY FIRSTORDER.id ASC
            LIMIT 1
          )  AS FIRST,
          (
            SELECT LASTORDER.exrate
            FROM EXORDERS LASTORDER
            WHERE
              (LASTORDER.date_acception = MAX(EO.date_acception)) AND
              (LASTORDER.currency_pair_id = EO.currency_pair_id) AND
              (LASTORDER.status_id = EO.status_id)
            ORDER BY LASTORDER.id DESC
            LIMIT 1
          )  AS LAST,

          MAX(EO.exrate) AS high24hr,
          MIN(EO.exrate) AS low24hr
        FROM EXORDERS EO
          JOIN CURRENCY_PAIR CP ON (CP.id = EO.currency_pair_id) AND (CP.hidden IS NOT TRUE)
        WHERE
          (currency_pair IS NULL OR currency_pair = "" OR UPPER(currency_pair) = "NULL" OR EO.currency_pair_id = (SELECT CURRENCY_PAIR.id
                                                                                                                  FROM CURRENCY_PAIR
                                                                                                                  WHERE CURRENCY_PAIR.ticker_name = currency_pair)) AND
          EO.status_id = 3 AND
          EO.date_acception >= now() - INTERVAL 24 HOUR
        GROUP BY CP.name, EO.currency_pair_id, EO.status_id;

    SELECT param_value
    FROM API_PARAMS
    WHERE param_name = "COINMARKETCAP_STATISTICS"
    INTO coinmarketcap_statistics;

    IF (coinmarketcap_statistics = "OFF") THEN
      SET currency_pair_name = "API DISABLED";
      SELECT
        currency_pair_name,
        currency_pair_id,
        status_id,
        first_date_acception,
        last_date_acception,
        predPoint,
        baseVolume,
        first,
        last,
        lowestAsk,
        highestBid,
        high24hr,
        low24hr,
        quoteVolume,
        isFrozen;

    ELSEIF (coinmarketcap_statistics = "ON") THEN

      BEGIN
        DECLARE eof BOOLEAN DEFAULT FALSE;
        DECLARE CONTINUE HANDLER FOR NOT FOUND SET eof = TRUE;

        set @@sql_mode = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION ';

        OPEN AGRIGATE;

        FETCH AGRIGATE
        INTO currency_pair_name, currency_pair_id, status_id, first_date_acception, last_date_acception, baseVolume, first, last, high24hr, low24hr;

        DROP TEMPORARY TABLE IF EXISTS COINMARKETCAP_STATISTICS_TMP_TBL;
        CREATE TEMPORARY TABLE COINMARKETCAP_STATISTICS_TMP_TBL
          SELECT
            currency_pair_name,
            currency_pair_id,
            status_id,
            first_date_acception,
            last_date_acception,
            predPoint,
            baseVolume,
            first,
            last,
            lowestAsk,
            highestBid,
            high24hr,
            low24hr,
            quoteVolume,
            isFrozen;

        DELETE FROM COINMARKETCAP_STATISTICS_TMP_TBL;

        WHILE (eof = FALSE) DO

          SET lowestAsk = NULL;
          SET highestBid = NULL;

          SELECT MIN(LOWESTASKORDER.exrate)
          FROM EXORDERS LOWESTASKORDER
          WHERE
            (LOWESTASKORDER.date_acception >= first_date_acception) AND
            (LOWESTASKORDER.currency_pair_id = currency_pair_id) AND
            (LOWESTASKORDER.status_id = status_id) AND
            (LOWESTASKORDER.operation_type_id = 3)
          INTO lowestAsk;

          SELECT MAX(HIGHESTBIDCORDER.exrate)
          FROM EXORDERS HIGHESTBIDCORDER
          WHERE
            (HIGHESTBIDCORDER.date_acception >= first_date_acception) AND
            (HIGHESTBIDCORDER.currency_pair_id = currency_pair_id) AND
            (HIGHESTBIDCORDER.status_id = status_id) AND
            (HIGHESTBIDCORDER.operation_type_id = 4)
          INTO highestBid;



          INSERT INTO COINMARKETCAP_STATISTICS_TMP_TBL VALUES (
            currency_pair_name,
            currency_pair_id,
            status_id,
            first_date_acception,
            last_date_acception,
            predPoint,
            baseVolume,
            first,
            last,
            lowestAsk,
            highestBid,
            high24hr,
            low24hr,
            quoteVolume,
            isFrozen);

          FETCH AGRIGATE
          INTO currency_pair_name, currency_pair_id, status_id, first_date_acception, last_date_acception, baseVolume, first, last, high24hr, low24hr;

        END WHILE;
      END;

      SELECT *
      FROM COINMARKETCAP_STATISTICS_TMP_TBL;
      DROP TABLE IF EXISTS COINMARKETCAP_STATISTICS_TMP_TBL;

      CLOSE AGRIGATE;

    END IF;

  END