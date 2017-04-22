ALTER TABLE TRANSACTION
	DROP INDEX source_type_source_id,
	ADD INDEX source_type_source_id_operation_type_id (source_type, source_id, operation_type_id);

ALTER TABLE MERCHANT
	DROP FOREIGN KEY FK_merchant_transaction_source_type;

DELETE FROM TRANSACTION_SOURCE_TYPE WHERE  id=2;
DELETE FROM TRANSACTION_SOURCE_TYPE WHERE  id=7;
DELETE FROM TRANSACTION_SOURCE_TYPE WHERE  id=8;
INSERT INTO TRANSACTION_SOURCE_TYPE (id, name) VALUES ('10', 'REFILL');


ALTER TABLE YANDEX_MONEY_PAYMENT
	DROP FOREIGN KEY merchant_image_id;

ALTER TABLE YANDEX_MONEY_PAYMENT
	DROP COLUMN merchant_image_id;

UPDATE MERCHANT SET service_bean_name='invoiceServiceImpl' WHERE  id=12;

UPDATE USER_COMMENT_TOPIC SET topic='REFILL_CURRENCY_WARNING' WHERE  id=3;

DROP TABLE CURRENCY_WARNING;

INSERT INTO USER_COMMENT_TOPIC (topic) VALUES ('WITHDRAW_CURRENCY_WARNING');