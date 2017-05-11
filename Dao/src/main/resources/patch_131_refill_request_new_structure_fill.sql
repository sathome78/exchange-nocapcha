SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM REFILL_REQUEST_PARAM;
DELETE FROM REFILL_REQUEST;
DELETE FROM REFILL_REQUEST_ADDRESS;

/*------------------------------*/

INSERT INTO REFILL_REQUEST_ADDRESS
 (currency_id, address, user_id, priv_key, pub_key, brain_priv_key)
 SELECT currency_id, address, user_id,

 (SELECT wif_priv_key
 FROM REFILL_REQUEST_TEMP RRTI
 WHERE RRTI.id = RRT.id),
 (SELECT pub_key
 FROM REFILL_REQUEST_TEMP RRTI
 WHERE RRTI.id = RRT.id),
 (SELECT brain_priv_key
 FROM REFILL_REQUEST_TEMP RRTI
 WHERE RRTI.id = RRT.id)

 FROM
 (SELECT currency_id, address, user_id, MAX(id) AS id
 FROM REFILL_REQUEST_TEMP
 WHERE address IS NOT NULL AND address <> ''
 GROUP BY currency_id, address
 ) RRT;

/*------------------------------*/
INSERT INTO REFILL_REQUEST
(id, amount, date_creation, status_id, status_modification_date, currency_id, user_id, commission_id, merchant_id, recipient_bank_id, admin_holder_id, import_note,
refill_request_address_id)
SELECT id, amount, date_creation, status_id, status_modification_date, currency_id, user_id, commission_id, merchant_id, recipient_bank_id, admin_holder_id, import_note,
(SELECT id FROM REFILL_REQUEST_ADDRESS RRA WHERE RRA.currency_id = RRT.currency_id AND RRA.address = RRT.address)
FROM REFILL_REQUEST_TEMP RRT;

SELECT COUNT(DISTINCT refill_request_address_id) FROM REFILL_REQUEST WHERE refill_request_address_id IS NOT NULL;

/*------------------------------*/

INSERT INTO REFILL_REQUEST_PARAM
(refill_request_id, param_name, param_value)
SELECT id, 'user_full_name', user_full_name
FROM REFILL_REQUEST_TEMP
WHERE user_full_name IS NOT NULL AND user_full_name <> '';

INSERT INTO REFILL_REQUEST_PARAM
(refill_request_id, param_name, param_value)
SELECT id, 'remark', remark
FROM REFILL_REQUEST_TEMP
WHERE remark IS NOT NULL AND remark <> '';

INSERT INTO REFILL_REQUEST_PARAM
(refill_request_id, param_name, param_value)
SELECT id, 'payer_bank_name', payer_bank_name
FROM REFILL_REQUEST_TEMP
WHERE payer_bank_name IS NOT NULL AND payer_bank_name <> '';

INSERT INTO REFILL_REQUEST_PARAM
(refill_request_id, param_name, param_value)
SELECT id, 'payer_bank_code', payer_bank_code
FROM REFILL_REQUEST_TEMP
WHERE payer_bank_code IS NOT NULL AND payer_bank_code <> '';

INSERT INTO REFILL_REQUEST_PARAM
(refill_request_id, param_name, param_value)
SELECT id, 'payer_account', payer_account
FROM REFILL_REQUEST_TEMP
WHERE payer_account IS NOT NULL AND payer_account <> '';

INSERT INTO REFILL_REQUEST_PARAM
(refill_request_id, param_name, param_value)
SELECT id, 'receipt_scan', receipt_scan
FROM REFILL_REQUEST_TEMP
WHERE receipt_scan IS NOT NULL AND receipt_scan <> '';

INSERT INTO REFILL_REQUEST_PARAM
(refill_request_id, param_name, param_value)
SELECT id, 'receipt_scan_name', receipt_scan_name
FROM REFILL_REQUEST_TEMP
WHERE receipt_scan_name IS NOT NULL AND receipt_scan_name <> '';

INSERT INTO REFILL_REQUEST_PARAM
(refill_request_id, param_name, param_value)
SELECT id, 'merchant_transaction_id', hash
FROM REFILL_REQUEST_TEMP
WHERE hash IS NOT NULL AND hash <> '';

INSERT INTO REFILL_REQUEST_PARAM
(refill_request_id, param_name, param_value)
SELECT id, 'merchant_transaction_id', merchant_transaction_id
FROM REFILL_REQUEST_TEMP
WHERE merchant_transaction_id IS NOT NULL AND merchant_transaction_id <> '' AND (hash IS NULL);

SET FOREIGN_KEY_CHECKS = 1;

/*==========================================================================================*/

В REFILL_REQUEST_ADDRESS загнали только одну запись для ключа currency_id+addres+user_id
Но в REFILL_REQUEST могут быть строки, у которых user_id другой - отличный от REFILL_REQUEST_ADDRESS,
что связано с тем, что раньше не контролировалась уникальность адреса для юзера

SELECT RR.*
FROM REFILL_REQUEST RR
JOIN REFILL_REQUEST_ADDRESS RRa ON (RRA.id=RR.refill_request_address_id)
WHERE RR.user_id <> RRA.user_id;

По идее в этой выборке должны быть
currency_id = 4 AND status_id = 12 (EXPIRED)
и
currency_id = 9 AND status_id = 4 (IN_PENDING)
Последнее связано с тараканами , которые были для currency_id = 9

SELECT DISTINCT RR.currency_id, RR.status_id
FROM REFILL_REQUEST RR
JOIN REFILL_REQUEST_ADDRESS RRa ON (RRA.id=RR.refill_request_address_id)
WHERE RR.user_id <> RRA.user_id

Если это так, то эти записи можем удалить.

DELETE RRC
FROM REFILL_REQUEST_CONFIRMATION RRC
JOIN REFILL_REQUEST RRI ON (RRI.id=RRC.refill_request_id)
JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id=RRI.refill_request_address_id)
WHERE RRI.user_id <> RRA.user_id;

DELETE RRP
FROM REFILL_REQUEST_PARAM RRP
JOIN REFILL_REQUEST RRI ON (RRI.id=RRP.refill_request_id)
JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id=RRI.refill_request_address_id)
WHERE RRI.user_id <> RRA.user_id;


DELETE RR
FROM REFILL_REQUEST RR
JOIN REFILL_REQUEST RRI ON (RRI.id=RR.id)
JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id=RRI.refill_request_address_id)
WHERE RRI.user_id <> RRA.user_id;

Теперь можем добавить контрольную связь - чтобы не было рассогласований пролей currency_id и user_id
между REFILL_REQUEST и REFILL_REQUEST_ADDRESS

ALTER TABLE REFILL_REQUEST_ADDRESS
	ADD INDEX id_currency_id_user_id (id, currency_id, user_id);
	
ALTER TABLE REFILL_REQUEST
	ADD CONSTRAINT FK_refill_request_refill_request_address_2 FOREIGN KEY (refill_request_address_id, currency_id, user_id) REFERENCES refill_request_address (id, currency_id, user_id);


/*==========================================================================================*/
