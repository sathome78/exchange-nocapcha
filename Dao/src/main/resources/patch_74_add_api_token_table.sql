CREATE TABLE API_AUTH_TOKEN (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `value` varchar(64) NOT NULL,
  `last_request` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_74_add_api_token_table', DEFAULT, 1);