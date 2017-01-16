package me.exrates.model.enums;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public enum ChatLang {

    EN("EN"),
    RU("RU"),
    CN("CN"),
    AR("AR");

    public final String val;

    ChatLang(final String lang) {
        val = lang;
    }

    public static ChatLang toInstance(final String val) {
        switch (val.toUpperCase()) {
            case "EN" : return EN;
            case "RU" : return RU;
            case "CN" : return CN;
            case "AR" : return AR;
            default:
                throw new IllegalArgumentException(val + " no such instance");
        }
    }
}
