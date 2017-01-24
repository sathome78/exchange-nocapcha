INSERT INTO USER_ROLE (id, name) VALUES (6, 'EXCHANGE');
INSERT INTO USER_ROLE (id, name) VALUES (7, 'VIP_USER');

ALTER TABLE COMMISSION
  CHANGE COLUMN `value` `default_value` DOUBLE(40,9) NOT NULL ;


CREATE TABLE COMMISSION_USER_ROLE (
  `commission_id` INT NOT NULL,
  `user_role_id` INT NOT NULL,
  `value` DOUBLE(40,9) NULL,
  PRIMARY KEY (`commission_id`, `user_role_id`),
  INDEX `comm_usr_role_usr_role_id_idx` (`user_role_id` ASC),
  CONSTRAINT `comm_usr_role_commission_id`
  FOREIGN KEY (`commission_id`)
  REFERENCES COMMISSION (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `comm_usr_role_usr_role_id`
  FOREIGN KEY (`user_role_id`)
  REFERENCES USER_ROLE (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

INSERT INTO COMMISSION_USER_ROLE
SELECT COMMISSION.id, USER_ROLE.id, COMMISSION.default_value FROM COMMISSION
JOIN USER_ROLE WHERE COMMISSION.editable = 1 AND USER_ROLE.id != 5;