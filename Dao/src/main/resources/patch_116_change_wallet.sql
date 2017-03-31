ALTER TABLE WALLET ADD active_balance_dec DECIMAL(40,9) UNSIGNED DEFAULT '0.000000000' NULL;
ALTER TABLE WALLET ADD reserved_balance_dec DECIMAL(40,9) UNSIGNED DEFAULT '0.000000000' NULL;
UPDATE WALLET SET active_balance_dec = active_balance, reserved_balance_dec = reserved_balance;
select count(*) from WALLET WHERE active_balance_dec != active_balance OR reserved_balance_dec != reserved_balance;
ALTER TABLE WALLET DROP active_balance;
ALTER TABLE WALLET DROP reserved_balance;
ALTER TABLE WALLET CHANGE active_balance_dec active_balance DECIMAL(40,9) unsigned DEFAULT '0.000000000';
ALTER TABLE WALLET CHANGE reserved_balance_dec reserved_balance DECIMAL(40,9) unsigned DEFAULT '0.000000000';

ALTER TABLE COMMISSION ADD value_dec DECIMAL(40,9) NULL;
UPDATE COMMISSION SET value_dec = value;
select count(*) from COMMISSION WHERE value_dec != value;
ALTER TABLE COMMISSION DROP value;
ALTER TABLE COMMISSION CHANGE value_dec value DECIMAL(40,9);


ALTER TABLE COMPANY_WALLET ADD balance_dec DECIMAL(40,9) DEFAULT '0.000000000' NULL;
ALTER TABLE COMPANY_WALLET ADD commission_balance_dec DECIMAL(40,9) DEFAULT '0.000000000' NULL;
UPDATE COMPANY_WALLET SET balance_dec = balance, commission_balance_dec = commission_balance;
select count(*) from COMPANY_WALLET WHERE balance_dec != balance OR commission_balance_dec != commission_balance;
ALTER TABLE COMPANY_WALLET DROP balance;
ALTER TABLE COMPANY_WALLET DROP commission_balance;
ALTER TABLE COMPANY_WALLET CHANGE balance_dec balance DECIMAL(40,9) DEFAULT '0.000000000';
ALTER TABLE COMPANY_WALLET CHANGE commission_balance_dec commission_balance DECIMAL(40,9) DEFAULT '0.000000000';

ALTER TABLE CURRENCY_LIMIT MODIFY min_sum DECIMAL(40,9);
ALTER TABLE CURRENCY_LIMIT MODIFY max_sum DECIMAL(40,9);

ALTER TABLE EXORDERS ADD exrate_dec DECIMAL(40,9) NULL;
ALTER TABLE EXORDERS ADD amount_base_dec DECIMAL(40,9) NULL;
ALTER TABLE EXORDERS ADD amount_convert_dec DECIMAL(40,9) NULL;
ALTER TABLE EXORDERS ADD commission_fixed_amount_dec DECIMAL(40,9) NULL;
UPDATE EXORDERS SET exrate_dec = exrate, amount_base_dec = amount_base,
  amount_convert_dec = amount_convert, commission_fixed_amount_dec = commission_fixed_amount;
select count(*) from EXORDERS WHERE exrate_dec != exrate OR amount_base_dec != amount_base
                                    OR amount_convert_dec != amount_convert OR commission_fixed_amount_dec != commission_fixed_amount;
ALTER TABLE EXORDERS DROP exrate;
ALTER TABLE EXORDERS DROP amount_base;
ALTER TABLE EXORDERS DROP amount_convert;
ALTER TABLE EXORDERS DROP commission_fixed_amount;
ALTER TABLE EXORDERS CHANGE amount_convert_dec amount_convert DECIMAL(40,9);
ALTER TABLE EXORDERS CHANGE commission_fixed_amount_dec commission_fixed_amount DECIMAL(40,9);
ALTER TABLE EXORDERS CHANGE exrate_dec exrate DECIMAL(40,9);
ALTER TABLE EXORDERS CHANGE amount_base_dec amount_base DECIMAL(40,9);

ALTER TABLE MERCHANT_CURRENCY MODIFY min_sum DECIMAL(40,9) DEFAULT '0.000000000';
ALTER TABLE MERCHANT_CURRENCY MODIFY merchant_input_commission DECIMAL(40,9) DEFAULT '0.000000000';
ALTER TABLE MERCHANT_CURRENCY MODIFY merchant_output_commission DECIMAL(40,9) DEFAULT '0.000000000';
ALTER TABLE MERCHANT_CURRENCY MODIFY merchant_fixed_commission DECIMAL(40,9) DEFAULT '0.000000000';
ALTER TABLE MERCHANT_CURRENCY MODIFY withdraw_auto_threshold_amount DECIMAL(40,9) NOT NULL DEFAULT '0.000000000';

ALTER TABLE REFERRAL_LEVEL MODIFY percent DECIMAL;

ALTER TABLE STOCK_EXRATE MODIFY price_low DECIMAL(40,9);
ALTER TABLE STOCK_EXRATE MODIFY price_high DECIMAL(40,9);
ALTER TABLE STOCK_EXRATE MODIFY price_last DECIMAL(40,9);
ALTER TABLE STOCK_EXRATE MODIFY price_buy DECIMAL(40,9);
ALTER TABLE STOCK_EXRATE MODIFY price_sell DECIMAL(40,9);
ALTER TABLE STOCK_EXRATE MODIFY volume DECIMAL(40,9);

ALTER TABLE TRANSACTION ADD amount_dec DECIMAL(40,9) NULL;
ALTER TABLE TRANSACTION ADD commission_amount_dec DECIMAL(40,9) NULL;
ALTER TABLE TRANSACTION MODIFY active_balance_before DECIMAL(40,9);
ALTER TABLE TRANSACTION MODIFY reserved_balance_before DECIMAL(40,9);
ALTER TABLE TRANSACTION MODIFY company_balance_before DECIMAL(40,9);
ALTER TABLE TRANSACTION MODIFY company_commission_balance_before DECIMAL(40,9);

UPDATE TRANSACTION SET amount_dec = amount, commission_amount_dec = commission_amount;
select count(*) from TRANSACTION WHERE amount_dec != amount OR commission_amount_dec != commission_amount;
ALTER TABLE TRANSACTION DROP amount;
ALTER TABLE TRANSACTION DROP commission_amount;
ALTER TABLE TRANSACTION CHANGE commission_amount_dec commission_amount DECIMAL(40,9);
ALTER TABLE TRANSACTION CHANGE amount_dec amount DECIMAL(40,9);
ALTER TABLE TRANSACTION
  MODIFY COLUMN amount DECIMAL(40,9) AFTER commission_id,
  MODIFY COLUMN commission_amount DECIMAL(40,9) AFTER amount;