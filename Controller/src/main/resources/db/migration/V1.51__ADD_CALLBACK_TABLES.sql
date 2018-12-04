CREATE TABLE IF NOT EXISTS CALLBACK_LOGS(
  user_id int(40),
  request_date varchar(50),
  response_date varchar(50),
  request_json BLOB,
  response_json varchar(255),
  response_code int,
  FOREIGN KEY (user_id) REFERENCES USER (id)
)CHARACTER SET utf8 COLLATE utf8_general_ci;