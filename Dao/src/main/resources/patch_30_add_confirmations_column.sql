ALTER TABLE TRANSACTION ADD COLUMN confirmation INT DEFAULT -1 ;

ALTER TABLE TRANSACTION CHANGE COLUMN confirmation confirmation INT DEFAULT -1 ;

UPDATE TRANSACTION SET confirmation = confirmation + 1 WHERE id  = 825;
SELECT *
FROM TRANSACTION;

UPDATE TRANSACTION SET confirmation = -1 ;

SELECT * FROM TRANSACTION WHERE id = 902;

UPDATE TRANSACTION SET confirmation = 0 WHERE id = 902;
SELECT * FROM PENDING_PAYMENT;

