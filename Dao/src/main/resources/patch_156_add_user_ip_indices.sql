CREATE INDEX user_ip__index_userid_regdate ON user_ip (user_id, registration_date);
CREATE INDEX user_ip__index_userid_last_regdate ON user_ip (user_id, last_registration_date);
CREATE INDEX user_ip__index_uid_regdate_lastregdate ON user_ip (user_id, registration_date, last_registration_date);