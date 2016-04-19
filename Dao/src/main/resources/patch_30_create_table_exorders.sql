/*ALTER TABLE ORDERS
ADD COLUMN exrate DOUBLE(40,9);

COMMIT;

UPDATE ORDERS
SET ORDERS.exrate = ORDERS.amount_buy/ORDERS.amount_sell
WHERE ORDERS.operation_type=3;

UPDATE ORDERS
SET ORDERS.exrate = ORDERS.amount_sell/ORDERS.amount_buy
WHERE ORDERS.operation_type=4;

COMMIT;*/

DROP TABLE IF EXISTS EXORDERS ;

CREATE TABLE EXORDERS (
  id int(40) NOT NULL,
  user_id int(40) NOT NULL,
  currency_pair int(40) NOT NULL,
  operation_type int(40) NOT NULL,
  exrate double(40,9) NOT NULL,
  amount_base double(40,9) NOT NULL,
  amount_convert double(40,9) NOT NULL,
  commission_fixed_amount double(40,9) NOT NULL,
  user_acceptor_id int(11) DEFAULT NULL,
  date_creation timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  date_acception timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY user_id (user_id),
  KEY currency_pair (currency_pair),
  CONSTRAINT fk_CURRENCY_PAIR FOREIGN KEY (currency_pair) REFERENCES currency_pair (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_USER_CREATOR FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_USER_ACCEPTOR FOREIGN KEY (user_acceptor_id) REFERENCES user (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_OPERATION_TYPE FOREIGN KEY (operation_type) REFERENCES operation_type (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO DATABASE_PATCH VALUES('patch_30_create_table_exorders',default,1);

COMMIT;
