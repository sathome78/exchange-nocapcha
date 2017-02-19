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
public enum InvoiceRequestStatusEnum implements InvoiceStatus {
  CREATED_USER(1) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        schemaMap.put(InvoiceActionTypeEnum.CONFIRM, CONFIRMED_USER);
        schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        schemaMap.put(InvoiceActionTypeEnum.EXPIRE, EXPIRED);
    }
  },
  CONFIRMED_USER(2) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        schemaMap.put(InvoiceActionTypeEnum.ACCEPT_MANUAL, ACCEPTED_ADMIN);
        schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
    }
  },
  REVOKED_USER(3) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  },
  ACCEPTED_ADMIN(4) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  },
  DECLINED_ADMIN(5) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        schemaMap.put(InvoiceActionTypeEnum.CONFIRM, CONFIRMED_USER);
        schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
        schemaMap.put(InvoiceActionTypeEnum.EXPIRE, EXPIRED);
    }
  },
  EXPIRED(6) {
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
    for (InvoiceRequestStatusEnum status : InvoiceRequestStatusEnum.class.getEnumConstants()) {
      status.initSchema(status.schemaMap);
    }
    /*check schemaMap*/
    getBeginState();
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

  public static InvoiceStatus getBeginState() {
    Set<InvoiceStatus> allNodesSet = collectAllSchemaMapNodesSet();
    List<InvoiceStatus> candidateList = Arrays.stream(InvoiceRequestStatusEnum.class.getEnumConstants())
        .filter(e -> !allNodesSet.contains(e))
        .collect(Collectors.toList());
    if (candidateList.size() == 0){
      System.out.println("begin state not found");
      throw new AssertionError();
    };
    if (candidateList.size() > 1){
      System.out.println("more than single begin state found: "+candidateList);
      throw new AssertionError();
    };
    return candidateList.get(0);
  }

  private static Set<InvoiceStatus> collectAllSchemaMapNodesSet() {
    Set<InvoiceStatus> result = new HashSet<>();
    Arrays.stream(InvoiceRequestStatusEnum.class.getEnumConstants())
        .forEach(e -> result.addAll(e.schemaMap.values()));
    return result;
  }

  private Integer code;

  InvoiceRequestStatusEnum(Integer code) {
    this.code = code;
  }

  @Override
  public Integer getCode() {
    return code;
  }


}

