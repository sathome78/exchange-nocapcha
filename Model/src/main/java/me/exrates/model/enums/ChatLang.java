package me.exrates.model.enums;

import java.util.stream.Stream;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public enum ChatLang {

    EN("EN"),
    RU("RU"),
    CN("CN"),
    AR("AR"),
    IN("IN"),
    KO("KO");

    public final String val;

    ChatLang(final String lang) {
        val = lang;
    }

    public static ChatLang toInstance(final String val) {
        return Stream.of(ChatLang.values()).filter(item -> item.val.equalsIgnoreCase(val))
                .findAny().orElseThrow(() -> new IllegalArgumentException(val + " no such instance"));
    }
}
