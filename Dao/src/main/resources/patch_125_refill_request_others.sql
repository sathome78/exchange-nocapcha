ALTER TABLE TRANSACTION
	DROP INDEX source_type_source_id,
	ADD INDEX source_type_source_id_operation_type_id (source_type, source_id, operation_type_id);