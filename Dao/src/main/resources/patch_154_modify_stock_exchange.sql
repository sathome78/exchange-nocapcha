ALTER TABLE stock_currency_pair ADD currency_pair_alias VARCHAR(20) NULL;

ALTER TABLE stock_exchange ADD is_active TINYINT(1) DEFAULT 1 NOT NULL;