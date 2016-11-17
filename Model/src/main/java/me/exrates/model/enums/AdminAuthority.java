package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedAuthorityException;

/**
 * Created by OLEG on 17.11.2016.
 */
public enum AdminAuthority {
    PROCESS_WITHDRAW(1),
    PROCESS_INVOICE(2),
    DELETE_ORDER(3),
    COMMENT_USER(4),
    MANAGE_SESSIONS(5),
    SET_CURRENCY_LIMIT(6),
    MANAGE_ACCESS(7),
    GRANT_MANAGE_ACCESS(8);

    private final int authority;

    AdminAuthority(int authority) {
        this.authority = authority;
    }

    public int getAuthority() {
        return authority;
    }

    public static AdminAuthority convert(int authority) {
        switch (authority) {
            case 1: return PROCESS_WITHDRAW;
            case 2: return PROCESS_INVOICE;
            case 3: return DELETE_ORDER;
            case 4: return COMMENT_USER;
            case 5: return MANAGE_SESSIONS;
            case 6: return SET_CURRENCY_LIMIT;
            case 7: return MANAGE_SESSIONS;
            case 8: return GRANT_MANAGE_ACCESS;
            default: throw new UnsupportedAuthorityException("Unsupported type of authority");
        }
    }
}
