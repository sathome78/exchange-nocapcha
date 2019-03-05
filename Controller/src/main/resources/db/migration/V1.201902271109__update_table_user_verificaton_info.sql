DROP TABLE IF EXISTS USER_VERIFICATION_INFO ;

CREATE TABLE USER_VERIFICATION_INFO (
  user_id INTEGER NOT NULL,
  last_names VARCHAR(255),
  first_names VARCHAR(255),
  born date,
  document_code VARCHAR(100),
  document_type VARCHAR(100) NOT NULL,
  image_encoded longblob,
  details TEXT)
  ENGINE = InnoDB;

ALTER TABLE USER_VERIFICATION_INFO
  ADD CONSTRAINT pk_user_verification_info PRIMARY KEY (user_id, document_type);

ALTER TABLE WITHDRAW_REQUEST
  ADD CONSTRAINT fk_user_id_on_user_verification_info FOREIGN KEY (user_id) REFERENCES USER(id) ON DELETE CASCADE;