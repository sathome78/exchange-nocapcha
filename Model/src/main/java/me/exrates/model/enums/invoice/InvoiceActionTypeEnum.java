package me.exrates.model.enums.invoice;

import me.exrates.model.exceptions.UnsupportedInvoiceActionTypeNameException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ValkSam on 18.02.2017.
 */
public enum InvoiceActionTypeEnum {
  CONFIRM,
  REVOKE,
  EXPIRE,
  BCH_EXAMINE,
  ACCEPT_MANUAL,
  ACCEPT_AUTO,
  DECLINE,
  PUT_FOR_MANUAL,
  PUT_FOR_AUTO,
  PUT_FOR_CONFIRM,
  POST;

  public static InvoiceActionTypeEnum convert(String name) {
    return Arrays.stream(InvoiceActionTypeEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedInvoiceActionTypeNameException(name));
  }

  public static List<InvoiceActionTypeEnum> convert(List<String> names) {
    return names.stream()
        .map(InvoiceActionTypeEnum::convert)
        .collect(Collectors.toList());
  }

}
