
INSERT INTO USER_ROLE (id, name) VALUES (8, 'TRADER');

INSERT INTO COMMISSION (operation_type, value, date, user_role)
  SELECT operation_type, value, NOW(), 8
  FROM COMMISSION WHERE user_role=4;

INSERT INTO CURRENCY_LIMIT(currency_id, operation_type_id, user_role_id, min_sum, max_sum)
  SELECT currency_id, operation_type_id, 8, min_sum, max_sum
  FROM CURRENCY_LIMIT WHERE user_role_id = 4;
