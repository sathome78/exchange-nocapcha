CREATE TABLE SERVICE_ALERTS (
  `alert_type` ENUM('UPDATE', 'TECHNICAL_WORKS') NOT NULL,
  `enable` BOOLEAN NOT NULL DEFAULT FALSE,
  `time_of_start` TIMESTAMP NULL DEFAULT NULL,
  `length` INTEGER,
  `launch_date` TIMESTAMP NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO SERVICE_ALERTS (alert_type, enable, time_of_start, length, launch_date)
VALUES ('UPDATE', FALSE, NULL, NULL, NULL),
  ('TECHNICAL_WORKS', FALSE, NULL, NULL, NULL)