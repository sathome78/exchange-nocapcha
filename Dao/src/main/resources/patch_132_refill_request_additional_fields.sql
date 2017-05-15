ALTER TABLE REFILL_REQUEST
  ADD COLUMN inner_transfer_hash VARCHAR(200) NULL;

ALTER TABLE MERCHANT
  ADD COLUMN to_main_account_transferring_needed BOOLEAN DEFAULT FALSE;

ALTER TABLE MERCHANT
  ADD COLUMN withdraw_transferring_confirm_needed BOOLEAN DEFAULT FALSE;

INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (15, 'ON_INNER_TRANSFERRING');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (16, 'WAITING_REVIEWING');
INSERT INTO REFILL_REQUEST_STATUS (id, name) VALUES (17, 'TAKEN_FOR_REFILL');

INSERT INTO WITHDRAW_REQUEST_STATUS (id, name) VALUES (13, 'ON_BCH_EXAM');
INSERT INTO WITHDRAW_REQUEST_STATUS (id, name) VALUES (14, 'WAITING_REVIEWING');
INSERT INTO WITHDRAW_REQUEST_STATUS (id, name) VALUES (15, 'TAKEN_FOR_WITHDRAW');