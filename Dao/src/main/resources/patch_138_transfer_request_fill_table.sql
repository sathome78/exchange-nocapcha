SELECT count(id) FROM USER_TRANSFER;
/*102*/


START TRANSACTION;

  INSERT INTO TRANSFER_REQUEST (id, user_id, recipient_user_id, merchant_id, amount, commission,
  date_creation, status_id, status_modification_date, currency_id, commission_id)
  SELECT id, from_user_id, to_user_id, (SELECT id FROM MERCHANT WHERE MERCHANT.name='SimpleTransfer'),
  amount, commission_amount, creation_date, 2, creation_date, currency_id, null
  FROM USER_TRANSFER;

COMMIT;

SELECT COUNT(id) FROM TRANSFER_REQUEST;
/*102*/


CREATE PROCEDURE fillMerchantCurrency(MerchantName varchar(30))
  BEGIN
    DECLARE i int DEFAULT (SELECT min(id) FROM CURRENCY);
    DECLARE N int DEFAULT (SELECT id FROM MERCHANT WHERE name = MerchantName);
    WHILE i <= (SELECT max(id) FROM CURRENCY) DO
      INSERT INTO MERCHANT_CURRENCY (merchant_id, currency_id, min_sum, withdraw_block, refill_block, transfer_block)
      VALUES (N, i, 0.000001, 1, 1, 0);
    SET i = (SELECT MIN(id) FROM CURRENCY WHERE id > i);
  END WHILE;
END;

CALL fillMerchantCurrency('SimpleTransfer');
CALL fillMerchantCurrency('VoucherTransfer');
CALL fillMerchantCurrency('VoucherFreeTransfer');

drop procedure if exists fillMerchantCurrency;

CREATE PROCEDURE fillMerchantImage(MerchantName varchar(30), Path VARCHAR(50), Vname VARCHAR(50))
  BEGIN
    DECLARE i int DEFAULT (SELECT min(id) FROM CURRENCY);
    DECLARE N int DEFAULT (SELECT id FROM MERCHANT WHERE name = MerchantName);
    WHILE i <= (SELECT max(id) FROM CURRENCY) DO
      INSERT INTO MERCHANT_IMAGE (merchant_id, image_path, image_name, currency_id) VALUES (N, Path, Vname, i);
      SET i = (SELECT MIN(id) FROM CURRENCY WHERE id > i);
    END WHILE;
  END;

CALL fillMerchantImage('SimpleTransfer', '/client/img/merchants/transfer.png', 'Transfer');
CALL fillMerchantImage('VoucherTransfer', '/client/img/merchants/voucher.png', 'Voucher');
CALL fillMerchantImage('VoucherFreeTransfer', '/client/img/merchants/voucher_free.png', 'Free voucher');

drop procedure if exists fillMerchantImage;