CREATE TABLE CHAT_AR (
  id INT PRIMARY KEY ,
  user_id INT NOT NULL ,
  body VARCHAR(256) NOT NULL,
  message_time DATETIME NOT NULL,
  FOREIGN KEY CHAT_RU(user_id) REFERENCES USER(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

INSERT INTO DATABASE_PATCH VALUES ('patch_82_add_arab_chat', default, 1);