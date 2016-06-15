package me.exrates.dao;

import me.exrates.model.ChatMessage;
import me.exrates.model.enums.ChatLang;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ChatDao {

    List<ChatMessage> findLastMessages(ChatLang lang);

}
