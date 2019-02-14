ALTER TABLE MERCHANT_CURRENCY ADD COLUMN merchant_fixed_commission_usd DOUBLE(40, 9) NOT NULL DEFAULT 0 AFTER merchant_fixed_commission;
ALTER TABLE MERCHANT_CURRENCY ADD COLUMN usd_rate NUMERIC(19, 8) NOT NULL DEFAULT 0 AFTER merchant_fixed_commission_usd;
ALTER TABLE MERCHANT_CURRENCY ADD recalculate_to_usd TINYINT(1) NOT NULL DEFAULT false;