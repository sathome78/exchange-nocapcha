ALTER TABLE NOTIFICATION_EVENT ADD default_send_notification TINYINT(1) NULL;
ALTER TABLE NOTIFICATION_EVENT ADD default_send_email TINYINT(1) NULL;
UPDATE NOTIFICATION_EVENT SET default_send_notification = 1;
UPDATE NOTIFICATION_EVENT SET default_send_email = 1 WHERE id IN(2, 3, 5);
UPDATE NOTIFICATION_EVENT SET default_send_email = 0 WHERE id IN(1, 4);

INSERT INTO DATABASE_PATCH VALUES ('patch_87_add_notification_option_defaults', default, 1);
