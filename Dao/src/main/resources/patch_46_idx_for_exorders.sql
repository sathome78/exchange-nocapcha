ALTER TABLE EXORDERS
ADD KEY currency_pair_status_accept (currency_pair_id, status_id, date_acception);

ALTER TABLE CURRENCY_PAIR
ADD CONSTRAINT currency_pair_curr1_fk FOREIGN KEY (currency1_id) REFERENCES CURRENCY (id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE CURRENCY_PAIR
ADD CONSTRAINT currency_pair_curr2_fk FOREIGN KEY (currency2_id) REFERENCES CURRENCY (id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE WALLET ADD UNIQUE KEY user_currency (user_id, currency_id);

INSERT INTO DATABASE_PATCH VALUES ('patch_46_idx_for_exorders and wallet', default, 1);