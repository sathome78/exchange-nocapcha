package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedUserActionOnInvoiceException;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * Created by Valk
 */
public enum UserActionOnInvoiceEnum {
  CONFIRM,
  REVOKE;

  public static UserActionOnInvoiceEnum convert(String name) {
    if (StringUtils.isEmpty(name)) {
      return CONFIRM;
    } else {
      return Arrays.stream(UserActionOnInvoiceEnum.class.getEnumConstants())
          .filter(e -> e.name().equals(name.toUpperCase()))
          .findAny()
          .orElseThrow(() -> new UnsupportedUserActionOnInvoiceException(name));
    }
  }

}
