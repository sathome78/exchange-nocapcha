ALTER TABLE TRANSACTION
	ALTER commission_id DROP DEFAULT;
ALTER TABLE TRANSACTION
	CHANGE COLUMN commission_id commission_id INT(11) NULL AFTER commission_amount;

ALTER TABLE TRANSACTION
	ALTER company_wallet_id DROP DEFAULT;
ALTER TABLE TRANSACTION
	CHANGE COLUMN company_wallet_id company_wallet_id INT(11) NULL AFTER user_wallet_id;