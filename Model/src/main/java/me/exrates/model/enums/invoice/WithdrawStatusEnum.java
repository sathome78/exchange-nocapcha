package me.exrates.model.enums.invoice;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.exceptions.UnsupportedInvoiceRequestStatusNameException;
import me.exrates.model.exceptions.UnsupportedInvoiceStatusForActionException;
import me.exrates.model.exceptions.UnsupportedNewsTypeIdException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ValkSam
 */
@Log4j2
public enum WithdrawStatusEnum implements InvoiceStatus {
  CREATED_USER(1) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.PUT_FOR_MANUAL, WAITING_MANUAL_POSTING);
      schemaMap.put(InvoiceActionTypeEnum.PUT_FOR_AUTO, WAITING_AUTO_POSTING);
      schemaMap.put(InvoiceActionTypeEnum.PUT_FOR_CONFIRM, WAITING_CONFIRMATION);
    }
  },
  WAITING_MANUAL_POSTING(2) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
      schemaMap.put(InvoiceActionTypeEnum.POST, POSTED_MANUAL);
      schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
    }
  },
  WAITING_AUTO_POSTING(3) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
      schemaMap.put(InvoiceActionTypeEnum.POST, POSTED_AUTO);
      schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
    }
  },
  WAITING_CONFIRMATION(4) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
      schemaMap.put(InvoiceActionTypeEnum.CONFIRM, WAITING_CONFIRMED_POSTING);
      schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
    }
  },
  WAITING_CONFIRMED_POSTING(5) {
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
      schemaMap.put(InvoiceActionTypeEnum.POST, POSTED_AUTO);
      schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
    }
  },
  REVOKED_USER(6) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  },
  DECLINED_ADMIN(7) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  },
  POSTED_MANUAL(8) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  },
  POSTED_AUTO(9) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  };

  final private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap = new HashMap<>();

  @Override
  public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
    return nextState(schemaMap, action)
        .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
  }

  @Override
  public Boolean availableForAction(InvoiceActionTypeEnum action) {
    return availableForAction(schemaMap, action);
  }

  static {
    for (WithdrawStatusEnum status : WithdrawStatusEnum.class.getEnumConstants()) {
      status.initSchema(status.schemaMap);
    }
    /*check schemaMap*/
    getBeginState();
  }

  public static List<InvoiceStatus> getAvailableForActionStatusesList(InvoiceActionTypeEnum action) {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> e.availableForAction(action))
        .collect(Collectors.toList());
  }

  public static List<InvoiceStatus> getAvailableForActionStatusesList(List<InvoiceActionTypeEnum> action) {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> action.stream().filter(e::availableForAction).findFirst().isPresent())
        .collect(Collectors.toList());
  }

  /**/

  public static WithdrawStatusEnum convert(int id) {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> e.code == id)
        .findAny()
        .orElseThrow(() -> new UnsupportedNewsTypeIdException(String.valueOf(id)));
  }

  public static WithdrawStatusEnum convert(String name) {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedInvoiceRequestStatusNameException(name));
  }

  public static InvoiceStatus getBeginState() {
    Set<InvoiceStatus> allNodesSet = collectAllSchemaMapNodesSet();
    List<InvoiceStatus> candidateList = Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> !allNodesSet.contains(e))
        .collect(Collectors.toList());
    if (candidateList.size() == 0) {
      System.out.println("begin state not found");
      throw new AssertionError();
    }
    if (candidateList.size() > 1) {
      System.out.println("more than single begin state found: " + candidateList);
      throw new AssertionError();
    }
    return candidateList.get(0);
  }

  public static Set<InvoiceStatus> getMiddleStatesSet() {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> !e.schemaMap.isEmpty())
        .collect(Collectors.toSet());
  }

  public static Set<InvoiceStatus> getEndStatesSet() {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> e.schemaMap.isEmpty())
        .collect(Collectors.toSet());
  }

  private static Set<InvoiceStatus> collectAllSchemaMapNodesSet() {
    Set<InvoiceStatus> result = new HashSet<>();
    Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .forEach(e -> result.addAll(e.schemaMap.values()));
    return result;
  }

  private Integer code;

  WithdrawStatusEnum(Integer code) {
    this.code = code;
  }

  @Override
  public Integer getCode() {
    return code;
  }


}

