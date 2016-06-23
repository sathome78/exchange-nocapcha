package me.exrates.model.dto;

import me.exrates.model.enums.ChatLang;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class ChatDto {

    private String body;
    private ChatLang lang;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ChatLang getLang() {
        return lang;
    }

    public void setLang(ChatLang lang) {
        this.lang = lang;
    }
}
