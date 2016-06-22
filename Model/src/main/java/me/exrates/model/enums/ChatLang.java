package me.exrates.model.enums;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public enum ChatLang {

    EN("EN"),
    RU("RU"),
    CN("CN");

    public final String val;

    ChatLang(final String lang) {
        val = lang;
    }

    public static ChatLang toInstance(final String val) {
        switch (val.toUpperCase()) {
            case "EN" : return EN;
            case "RU" : return RU;
            case "CN" : return CN;
            default:
                throw new IllegalArgumentException(val + " no such instance");
        }
    }
}
