ALTER TABLE NOTIFICATION MODIFY title VARCHAR(70) NOT NULL;


ALTER TABLE WITHDRAW_REQUEST ADD recipient_bank_name VARCHAR(50) NULL;
ALTER TABLE WITHDRAW_REQUEST ADD recipient_bank_code VARCHAR(10) NULL;
ALTER TABLE WITHDRAW_REQUEST ADD user_full_name VARCHAR(100) NULL;
ALTER TABLE WITHDRAW_REQUEST ADD remark VARCHAR(300) NULL;

INSERT INTO DATABASE_PATCH VALUES ('patch_96_change_withdraw', default, 1);
