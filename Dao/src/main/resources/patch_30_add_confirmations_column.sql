ALTER TABLE TRANSACTION ADD COLUMN confirmation INT DEFAULT -1 ;

UPDATE TRANSACTION SET confirmation = -1 ;

INSERT INTO DATABASE_PATCH VALUES('patch_30_add_confirmations_column',default,1);