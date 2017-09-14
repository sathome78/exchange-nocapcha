ALTER TABLE INVOICE_BANK ADD hidden TINYINT(1) DEFAULT 0 NOT NULL;
UPDATE INVOICE_BANK SET hidden = 1 WHERE name = 'MANDIRI';
INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient) VALUES
  ((SELECT id FROM CURRENCY where name = 'IDR'), 'BCA', '3150970261', 'BASUNI'),
  ((SELECT id FROM CURRENCY where name = 'IDR'), 'BRI', '124701007080505', 'BASUNI'),
  ((SELECT id FROM CURRENCY where name = 'IDR'), 'BNI', '0474501360', 'BASUNI');


ALTER TABLE merchant_currency ADD subtract_merchant_commission_for_withdraw TINYINT(1) DEFAULT 0 NOT NULL;

ALTER TABLE withdraw_request ADD merchant_commission DECIMAL(40,9) NULL AFTER commission;