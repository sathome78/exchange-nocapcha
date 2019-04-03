CREATE TABLE IF NOT EXISTS IEO_RESULT
(
    claim_id         INT(11)                                              NOT NULL,
    ieo_id           INT(11)                                              NOT NULL,
    available_amount DOUBLE(40, 9)                    DEFAULT 0.000000000 NULL,
    status           ENUM ('success', 'fail', 'none') DEFAULT 'none'      NOT NULL,
    FOREIGN KEY (claim_id) REFERENCES IEO_CLAIM (id),
    FOREIGN KEY (ieo_id) REFERENCES IEO_DETAILS (id))
)
    ENGINE = INNODB;