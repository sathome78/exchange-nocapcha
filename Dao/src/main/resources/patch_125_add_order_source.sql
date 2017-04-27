UPDATE ORDER_STATUS SET name = 'split_closed' where id = 7;
ALTER TABLE EXORDERS ADD order_source_id INT NULL;

ALTER TABLE EXORDERS ADD CONSTRAINT exorders___fk_source_id
FOREIGN KEY (order_source_id) REFERENCES EXORDERS (id);