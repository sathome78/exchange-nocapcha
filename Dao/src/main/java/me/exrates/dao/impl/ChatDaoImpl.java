package me.exrates.dao.impl;

import me.exrates.dao.ChatDao;
import me.exrates.model.ChatMessage;
import me.exrates.model.enums.ChatLang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class ChatDaoImpl implements ChatDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ChatDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ChatMessage> findLastMessages(ChatLang lang) {
        final String sql = "SELECT c.user_id, c.message, USER.nickname FROM CHAT_".concat(lang.val)
                            .concat(" as c INNER JOIN USER ON c.user_id = USER.id ORDER BY c.datetime DESC LIMIT 50");
        return jdbcTemplate.query(sql, (resultSet, i) -> {
            final ChatMessage message = new ChatMessage();
            message.setNickname(resultSet.getString("nickname"));
            message.setUserId(resultSet.getInt("user_id"));
            message.setBody(resultSet.getString("message"));
            return message;
        });
    }
}
