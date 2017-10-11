CREATE INDEX user_ip__index_userid_regdate ON USER_IP (user_id, registration_date);
CREATE INDEX user_ip__index_userid_last_regdate ON USER_IP (user_id, last_registration_date);
CREATE INDEX user_ip__index_uid_regdate_lastregdate ON USER_IP (user_id, registration_date, last_registration_date);