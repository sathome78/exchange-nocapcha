CREATE TABLE IF NOT EXISTS IEO_RESULT
(
  id       INT(11) PRIMARY KEY                                               NOT NULL AUTO_INCREMENT,
  claim_id INT(11)                                                           NOT NULL,
  status   enum ('success', 'fail', 'none') default 'none'                   NOT NULL,
  FOREIGN KEY (claim_id) REFERENCES IEO_CLAIM (id)
)
  ENGINE = INNODB;