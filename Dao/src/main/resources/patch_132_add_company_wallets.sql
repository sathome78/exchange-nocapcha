insert into COMPANY_WALLET (currency_id)
  select id from CURRENCY c where not exists(SELECT * FROM COMPANY_WALLET cw where c.id = cw.currency_id);