ALTER TABLE ORDERS
ADD COLUMN exrate DOUBLE(40,9);

COMMIT;

UPDATE ORDERS
SET ORDERS.exrate = ORDERS.amount_buy/ORDERS.amount_sell
WHERE ORDERS.operation_type=3;

UPDATE ORDERS
SET ORDERS.exrate = ORDERS.amount_sell/ORDERS.amount_buy
WHERE ORDERS.operation_type=4;

COMMIT;
