package me.exrates.model.enums;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.InvoiceRequestStatus;
import me.exrates.model.exceptions.UnsupportedInvoiceRequestStatusNameException;
import me.exrates.model.exceptions.UnsupportedInvoiceRequestStatusSetNameException;
import me.exrates.model.exceptions.UnsupportedNewsTypeIdException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ValkSam
 */
@Log4j2
public enum InvoiceRequestStatusEnum {
  CREATED_USER(1),
  CONFIRMED_USER(2),
  REVOKED_USER(3),
  ACCEPTED_ADMIN(4),
  DECLINED_ADMIN(5),
  EXPIRED(6);

  private Integer code;

  InvoiceRequestStatusEnum(Integer code) {
    this.code = code;
  }

  public static Boolean availableToConfirm(InvoiceRequest invoiceRequest) {
    InvoiceRequestStatusEnum status = invoiceRequest.getInvoiceRequestStatus();
    return status == CREATED_USER || status == DECLINED_ADMIN;
  }

  public static Boolean revokeable(InvoiceRequest invoiceRequest) {
    InvoiceRequestStatusEnum status = invoiceRequest.getInvoiceRequestStatus();
    return status == CREATED_USER || status == DECLINED_ADMIN;
  }

  public static List<InvoiceRequestStatusEnum> getMayExpireStatusList() {
    return new ArrayList<InvoiceRequestStatusEnum>() {{
      add(CREATED_USER);
      add(DECLINED_ADMIN);
    }};
  }

  public static List<InvoiceRequestStatusEnum> getMayBeConfirmedStatusList() {
    return new ArrayList<InvoiceRequestStatusEnum>() {{
      add(CREATED_USER);
      add(DECLINED_ADMIN);
    }};
  }

  public static List<InvoiceRequestStatusEnum> getMayBeAcceptedStatusList() {
    return new ArrayList<InvoiceRequestStatusEnum>() {{
      add(CONFIRMED_USER);
    }};
  }

  public static List<InvoiceRequestStatusEnum> getStatusSet(String setName) {
    switch (setName.toLowerCase()) {
      case "confirmable": {
        return getMayBeConfirmedStatusList();
      }
      case "revokeable": {
        return getMayExpireStatusList();
      }
      case "acceptable": {
        return getMayBeAcceptedStatusList();
      }
      default: {
        throw new UnsupportedInvoiceRequestStatusSetNameException(setName);
      }
    }
  }

  public static InvoiceRequestStatusEnum convert(InvoiceRequestStatus invoiceRequestStatus) {
    return convert(invoiceRequestStatus.getId());
  }

  public static InvoiceRequestStatusEnum convert(int id) {
    return Arrays.stream(InvoiceRequestStatusEnum.class.getEnumConstants())
        .filter(e -> e.code == id)
        .findAny()
        .orElseThrow(() -> new UnsupportedNewsTypeIdException(String.valueOf(id)));
  }

  public static InvoiceRequestStatusEnum convert(String name) {
    return Arrays.stream(InvoiceRequestStatusEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedInvoiceRequestStatusNameException(name));
  }

  public Integer getCode() {
    return code;
  }
}
