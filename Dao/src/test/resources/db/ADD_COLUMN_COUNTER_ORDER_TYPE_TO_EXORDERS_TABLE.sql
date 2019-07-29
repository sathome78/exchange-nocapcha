ALTER TABLE EXORDERS
    ADD COLUMN counter_order_type enum ('LIMIT', 'ICO', 'MARKET') default 'LIMIT' not null AFTER counter_order_id;