START TRANSACTION;

INSERT INTO TRANSFER_REQUEST (id, user_id, recipient_user_id, merchant_id, amount, commission,
date_creation, status_id, status_modification_date, currency_id, commission_id)
 SELECT id, from_user_id, to_user_id, (SELECT id FROM MERCHANT WHERE MERCHANT.name='SimpleTransfer'),
 amount, commission_amount, creation_date, 2, creation_date, currency_id, null
 FROM USER_TRANSFER;

COMMIT;