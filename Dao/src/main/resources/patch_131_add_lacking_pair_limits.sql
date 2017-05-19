select id FROM CURRENCY_PAIR CP where not exists (select * from CURRENCY_PAIR_LIMIT where currency_pair_id = CP.id);

INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, USER_ROLE.id, ORDER_TYPE.id, 0, 99999999999
  FROM CURRENCY_PAIR CP
    JOIN USER_ROLE
    JOIN ORDER_TYPE
  WHERE not exists (select * from CURRENCY_PAIR_LIMIT where currency_pair_id = CP.id);