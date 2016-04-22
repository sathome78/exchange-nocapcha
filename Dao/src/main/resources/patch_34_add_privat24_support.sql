
INSERT INTO `birzha`.`MERCHANT` (`description`, `name`) VALUES ('Privat24', 'Privat24');

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum)
  VALUES ((SELECT id from MERCHANT WHERE name=Privat24),
          (SELECT id from CURRENCY WHERE name=UAH),
          0.01000000);

INSERT INTO DATABASE_PATCH VALUES('patch_34_added_privat24_support',default,1);

 SELECT WITHDRAW_REQUEST.acceptance, WITHDRAW_REQUEST.wallet, WITHDRAW_REQUEST.processed_by,  
USER.email,(SELECT EMAIL from USER WHERE id = WITHDRAW_REQUEST.processed_by) as admin_email,  
TRANSACTION.id,TRANSACTION.amount,TRANSACTION.commission_amount,TRANSACTION.datetime,  
TRANSACTION.operation_type_id,TRANSACTION.provided,TRANSACTION.confirmation, WALLET.id,WALLET.active_balance,  
WALLET.reserved_balance,WALLET.currency_id,COMPANY_WALLET.id,COMPANY_WALLET.balance,  
COMPANY_WALLET.commission_balance,COMMISSION.id,COMMISSION.date,COMMISSION.value, 
CURRENCY.id,CURRENCY.description,CURRENCY.name,MERCHANT.id,MERCHANT.name,MERCHANT.description  
FROM WITHDRAW_REQUEST  
INNER JOIN TRANSACTION ON TRANSACTION.id = WITHDRAW_REQUEST.transaction_id  
INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id  
INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id  
INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id  
INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id  
INNER JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id  
INNER JOIN USER ON WALLET.user_id = USER.id;