package me.exrates.model.enums.invoice;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.exceptions.UnsupportedInvoiceRequestStatusNameException;
import me.exrates.model.exceptions.UnsupportedInvoiceStatusForActionException;
import me.exrates.model.exceptions.UnsupportedNewsTypeIdException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ValkSam
 */
@Log4j2
public enum InvoiceRequestStatusEnum implements InvoiceStatus {
  CREATED_USER(1) {
    private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap;

    @Override
    public void initSchema() {
      schemaMap = new HashMap<InvoiceActionTypeEnum, InvoiceStatus>() {{
        put(InvoiceActionTypeEnum.CONFIRM, CONFIRMED_USER);
        put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        put(InvoiceActionTypeEnum.EXPIRE, EXPIRED);
      }};
    }

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
      return nextState(schemaMap, action)
          .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
      return availableForAction(schemaMap, action);
    }
  },
  CONFIRMED_USER(2) {
    private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap;

    @Override
    public void initSchema() {
      schemaMap = new HashMap<InvoiceActionTypeEnum, InvoiceStatus>() {{
        put(InvoiceActionTypeEnum.ACCEPT_MANUAL, ACCEPTED_ADMIN);
        put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
      }};
    }

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
      return nextState(schemaMap, action)
          .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
      return availableForAction(schemaMap, action);
    }
  },
  REVOKED_USER(3) {
    private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap;

    @Override
    public void initSchema() {
      schemaMap = new HashMap<InvoiceActionTypeEnum, InvoiceStatus>() {{
      }};
    }

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
      return nextState(schemaMap, action)
          .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
      return availableForAction(schemaMap, action);
    }
  },
  ACCEPTED_ADMIN(4) {
    private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap;

    @Override
    public void initSchema() {
      schemaMap = new HashMap<InvoiceActionTypeEnum, InvoiceStatus>() {{
      }};
    }

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
      return nextState(schemaMap, action)
          .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
      return availableForAction(schemaMap, action);
    }
  },
  DECLINED_ADMIN(5) {
    private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap;

    @Override
    public void initSchema() {
      schemaMap = new HashMap<InvoiceActionTypeEnum, InvoiceStatus>() {{
        put(InvoiceActionTypeEnum.CONFIRM, CONFIRMED_USER);
        put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        put(InvoiceActionTypeEnum.EXPIRE, EXPIRED);
      }};
    }

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
      return nextState(schemaMap, action)
          .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
      return availableForAction(schemaMap, action);
    }
  },
  EXPIRED(6) {
    private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap;

    public void initSchema() {
      schemaMap = new HashMap<InvoiceActionTypeEnum, InvoiceStatus>() {{
      }};
    }

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
      return nextState(schemaMap, action)
          .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
      return availableForAction(schemaMap, action);
    }
  };

  static {
    for (InvoiceRequestStatusEnum status : InvoiceRequestStatusEnum.class.getEnumConstants()) {
      status.initSchema();
    }
  }

  public static List<InvoiceStatus> getAvailableForActionStatusesList(InvoiceActionTypeEnum action) {
    return Arrays.stream(InvoiceRequestStatusEnum.class.getEnumConstants())
        .filter(e -> e.availableForAction(action))
        .collect(Collectors.toList());
  }

  public static List<InvoiceStatus> getAvailableForActionStatusesList(List<InvoiceActionTypeEnum> action) {
    return Arrays.stream(InvoiceRequestStatusEnum.class.getEnumConstants())
        .filter(e -> action.stream().filter(e::availableForAction).findFirst().isPresent())
        .collect(Collectors.toList());
  }

  /**/

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

  private Integer code;

  InvoiceRequestStatusEnum(Integer code) {
    this.code = code;
  }

  public Integer getCode() {
    return code;
  }


}

