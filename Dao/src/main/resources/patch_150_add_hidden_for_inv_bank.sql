ALTER TABLE INVOICE_BANK ADD hidden TINYINT(1) DEFAULT 0 NOT NULL;
UPDATE INVOICE_BANK SET hidden = 1 WHERE name = 'MANDIRI';
INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient, bank_details) VALUES
  ((SELECT id FROM CURRENCY where name = 'IDR'), '', '', '', '')