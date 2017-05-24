ALTER TABLE INVOICE_BANK ADD bank_details VARCHAR(300) NULL;

INSERT INTO INVOICE_BANK (currency_id, name, account_number, recipient, bank_details)
VALUES ((SELECT id FROM CURRENCY WHERE name = 'VND'),
        'Vietcombank VND', '‎0251002734890', 'PHAM TRUONG HO', 'Bank name: VIETCOMBANK CHI NHÁNH BÌNH TÂY'),
  ((SELECT id FROM CURRENCY WHERE name = 'AED'),
   'Diamond Bank PLC', '‎‎068520105558301', NULL, 'Branch: Dubai Mall Branch\nSWIFT Code / BIC : DUIBAEAD\nIBAN: AE330240068520105558301'),
  ((SELECT id FROM CURRENCY WHERE name = 'CNY'),
   'China Merchants Bank', '‎‎110923954410401', NULL, '公司名稱：北京艾特天下网络科技有限公司\n銀行名稱: 招商银行\n地址：招商银行北京东直门支行');