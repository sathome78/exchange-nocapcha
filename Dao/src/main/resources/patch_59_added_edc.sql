INSERT INTO CURRENCY (name, description) VALUES ('EDC', 'E-DinarCoin');

INSERT INTO MERCHANT(description, name) VALUES ('E-DinarCoin', 'EDC');

SELECT * FROM MERCHANT_CURRENCY;

INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, merchant_commission) VALUES (
  (SELECT id FROM MERCHANT WHERE name = 'EDC'),
  (SELECT id FROM CURRENCY WHERE name ='EDC'),
  0.01,
  0
);

SELECT * FROM MERCHANT_IMAGE;

INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES (
  (SELECT id FROM MERCHANT WHERE name = 'EDC'),
  '/client/img/merchants/edc.png',
  'E-DinarCoin',
  (SELECT id FROM CURRENCY WHERE name ='EDC')
);

insert into WALLET (user_id, currency_id) select id, (SELECT id FROM CURRENCY WHERE name ='EDC') FROM USER;

INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_59_added_edc', DEFAULT, 1);
