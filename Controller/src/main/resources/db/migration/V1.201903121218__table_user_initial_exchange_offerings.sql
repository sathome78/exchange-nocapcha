CREATE TABLE IF NOT EXISTS USER_INITIAL_EXCHANGE_OFFERINGS (
  id    INT           NOT NULL AUTO_INCREMENT,
  email VARCHAR(200)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX id_UNIQUE (`id` ASC),
  UNIQUE INDEX email_UNIQUE (`email` ASC)
) CHARSET=utf8;