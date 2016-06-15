INSERT INTO CHAT_EN (user_id, message) VALUES (1, 'Blablablabla');
INSERT INTO CHAT_EN (user_id, message) VALUES (1, 'Blabl123123la');


SELECT c.user_id, c.message, c.datetime, USER.nickname from CHAT_EN as c INNER JOIN USER ON c.user_id = USER.id ORDER BY c.datetime DESC LIMIT 50;