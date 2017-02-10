CREATE TABLE `INVOICE_BANK` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_id` int(11) DEFAULT NULL,
  `name` varchar(60) DEFAULT NULL,
  `account_number` varchar(60) DEFAULT NULL,
  `recipient` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `invoice_bank___fk_currency` (`currency_id`),
  CONSTRAINT `invoice_bank___fk_currency` FOREIGN KEY (`currency_id`) REFERENCES `CURRENCY` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient) VALUES 
  ((SELECT id FROM CURRENCY WHERE name = 'IDR'), 'BCA', '3150963141', 'Nanda Rizal Pahlewi');
INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient) VALUES
  ((SELECT id FROM CURRENCY WHERE name = 'IDR'), 'MANDIRI', '1440099965557', 'Nanda Rizal Pahlewi');
INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient) VALUES
  ((SELECT id FROM CURRENCY WHERE name = 'IDR'), 'BNI', '0483087786', 'Nanda Rizal Pahlewi');
INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient) VALUES
  ((SELECT id FROM CURRENCY WHERE name = 'IDR'), 'BRI', '057901000435567', 'Nanda Rizal Pahlewi');

ALTER TABLE INVOICE_REQUEST ADD remark VARCHAR(300) NULL;
ALTER TABLE INVOICE_REQUEST ADD user_full_name VARCHAR(250) NULL;
ALTER TABLE INVOICE_REQUEST ADD bank_id INT NULL;
ALTER TABLE INVOICE_REQUEST
  MODIFY COLUMN acceptance_time DATETIME AFTER user_full_name,
  MODIFY COLUMN acceptance_user_id INT(11) AFTER user_full_name;
ALTER TABLE INVOICE_REQUEST ADD payer_bank_name VARCHAR(50) NULL;
ALTER TABLE INVOICE_REQUEST ADD payer_account VARCHAR(100) NULL;
ALTER TABLE INVOICE_REQUEST CHARACTER SET utf8;
ALTER TABLE INVOICE_REQUEST MODIFY remark VARCHAR(300) CHARACTER SET utf8;
ALTER TABLE INVOICE_REQUEST MODIFY user_full_name VARCHAR(250) CHARACTER SET utf8;
ALTER TABLE INVOICE_REQUEST MODIFY payer_bank_name VARCHAR(200) CHARACTER SET utf8;
ALTER TABLE INVOICE_REQUEST MODIFY payer_account VARCHAR(100) CHARACTER SET utf8;

