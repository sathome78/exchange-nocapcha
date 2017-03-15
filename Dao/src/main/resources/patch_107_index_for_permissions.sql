ALTER TABLE USER_CURRENCY_INVOICE_OPERATION_PERMISSION
	ADD INDEX user_id_currency_id_operation_direction (user_id, currency_id, operation_direction);

