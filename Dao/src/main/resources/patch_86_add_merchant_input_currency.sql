ALTER TABLE MERCHANT_CURRENCY CHANGE merchant_commission merchant_output_commission DOUBLE(40,9) DEFAULT '0.000000000';
ALTER TABLE MERCHANT_CURRENCY ADD merchant_input_commission DOUBLE(40,9) DEFAULT '0.000000000' AFTER min_sum;

INSERT INTO DATABASE_PATCH VALUES ('patch_89_add_merchant_input_commission', default, 1);
