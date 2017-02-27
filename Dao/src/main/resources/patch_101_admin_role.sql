
INSERT INTO USER_ROLE (id, name) VALUES (9, 'FIN_OPERATOR');

INSERT INTO COMMISSION (operation_type, value, date, user_role)
  SELECT operation_type, value, NOW(), 9
  FROM COMMISSION WHERE user_role=1;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT currency_id, operation_type_id, 9, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE user_role_id = 1;
  
  
CREATE TABLE INVOICE_OPERATION_PERMISSION (
	id INT(11) NOT NULL AUTO_INCREMENT,
	name VARCHAR(20) NOT NULL,
	PRIMARY KEY (id)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO INVOICE_OPERATION_PERMISSION (id, name) VALUES (1, 'VIEW_ONLY');
INSERT INTO INVOICE_OPERATION_PERMISSION (id, name) VALUES (2, 'ACCEPT_DECLINE');

CREATE TABLE USER_CURRENCY_INVOICE_OPERATION_PERMISSION (
	user_id INT(11) NOT NULL,
	currency_id INT(11) NOT NULL,
	invoice_operation_permission_id INT(11) NOT NULL,
	INDEX FK_user_currency_invoice_permission_invoice_permission (invoice_operation_permission_id),
	CONSTRAINT FK_user_currency_invoice_permission_invoice_permission FOREIGN KEY (invoice_operation_permission_id) REFERENCES INVOICE_OPERATION_PERMISSION (id)
)
ENGINE=InnoDB;

