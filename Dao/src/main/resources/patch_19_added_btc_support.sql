INSERT INTO MERCHANT (description, name) VALUES ("Blockchain","blockchain");

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
VALUES ((SELECT id from MERCHANT WHERE name="blockchain"),
        (SELECT id from CURRENCY WHERE name="BTC"),
        0.00000001);

CREATE TABLE IF NOT EXISTS PENDING_BLOCKCHAIN_PAYMENT (
  amount DOUBLE(40,9) NOT NULL ,
  invoice_id INT NOT NULL ,
  address VARCHAR(36) NOT NULL,
  PRIMARY KEY (invoice_id)
);

ALTER TABLE PENDING_BLOCKCHAIN_PAYMENT ADD FOREIGN KEY PENDING_BLOCKCHAIN_PAYMENT(invoice_id)
REFERENCES TRANSACTION (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE TABLE IF NOT EXISTS BTC_TRANSACTION (
  hash VARCHAR(64) NOT NULL,
  amount DOUBLE(40,9) NOT NULL,
  transaction_id INT,
  PRIMARY KEY (hash)
);

ALTER TABLE BTC_TRANSACTION ADD FOREIGN KEY BTC_TRANSACTION(transaction_id) REFERENCES TRANSACTION(id)
  ON UPDATE RESTRICT ON DELETE RESTRICT ;

INSERT INTO `birzha`.`USER_ROLE` (`id`, `name`) VALUES ('5', 'ROLE_CHANGE_PASSWORD');
INSERT INTO DATABASE_PATCH VALUES('patch_19_added_btc_support',default,1);