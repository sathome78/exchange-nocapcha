INSERT INTO USER_ROLE (id, name) VALUES (6, 'EXCHANGE');
INSERT INTO USER_ROLE (id, name) VALUES (7, 'VIP_USER');

ALTER TABLE COMMISSION
  DROP COLUMN `editable`,
  ADD COLUMN `user_role` INT(40) NULL AFTER `date`,
  ADD INDEX `commission_fk_usr_role_idx` (`user_role` ASC);
ALTER TABLE COMMISSION
  ADD CONSTRAINT `commission_fk_usr_role`
FOREIGN KEY (`user_role`)
REFERENCES USER_ROLE (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE COMMISSION
  ADD UNIQUE `unique_optype_user_role_idx`(`operation_type`, `user_role`);

UPDATE COMMISSION SET user_role = 4;

CREATE TEMPORARY TABLE IF NOT EXISTS TEMP_COMM AS (SELECT * FROM COMMISSION);

INSERT INTO COMMISSION(operation_type, date, value, user_role) SELECT TEMP_COMM.operation_type,
  CURRENT_TIMESTAMP, TEMP_COMM.value, USER_ROLE.id
FROM TEMP_COMM
JOIN USER_ROLE WHERE USER_ROLE.id != 4 AND USER_ROLE.id != 5;

DROP TEMPORARY TABLE IF EXISTS TEMP_COMM;

CREATE TABLE CURRENCY_LIMIT (
  `id` int(40) NOT NULL AUTO_INCREMENT,
  `currency_id` int(40) NOT NULL,
  `operation_type_id` int(40) NOT NULL,
  `user_role_id` int(40) NOT NULL,
  `min_sum` double(40,9) DEFAULT NULL,
  `max_sum` double(40,9) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `currency_limit___fk_currency` (`currency_id`),
  KEY `currency_limit___fk_optype` (`operation_type_id`),
  KEY `currency_limit___fk_usr_role` (`user_role_id`),
  CONSTRAINT `currency_limit___fk_currency` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`),
  CONSTRAINT `currency_limit___fk_optype` FOREIGN KEY (`operation_type_id`) REFERENCES `OPERATION_TYPE` (`id`),
  CONSTRAINT `currency_limit___fk_usr_role` FOREIGN KEY (`user_role_id`) REFERENCES `USER_ROLE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE CURRENCY_LIMIT
  ADD UNIQUE `currency_limit__unique_idx`(`currency_id`, `operation_type_id`, `user_role_id`);

INSERT INTO CURRENCY_LIMIT (currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT CURRENCY.id AS currency_id, OPERATION_TYPE.id AS op_type, USER_ROLE.id AS role, CURRENCY.min_withdraw_sum AS min, NULL AS max
  FROM CURRENCY
    JOIN OPERATION_TYPE
    JOIN USER_ROLE
  WHERE CURRENCY.hidden IS NOT TRUE AND OPERATION_TYPE.id IN (2, 9) AND USER_ROLE.id != 5
  ORDER BY currency_id, op_type, role;