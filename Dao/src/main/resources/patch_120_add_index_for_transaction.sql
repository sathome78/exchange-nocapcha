ALTER TABLE TRANSACTION
    ADD INDEX operation_type_id_status_id_provided_source_type (operation_type_id, status_id, provided,  source_type);