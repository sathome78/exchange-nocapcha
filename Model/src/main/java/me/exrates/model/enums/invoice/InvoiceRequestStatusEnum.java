package me.exrates.model.enums.invoice;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.exceptions.*;

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
      schemaMap.put(InvoiceActionTypeEnum.CONFIRM_USER, CONFIRMED_USER);
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
      schemaMap.put(InvoiceActionTypeEnum.CONFIRM_USER, CONFIRMED_USER);
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
    if (action.isAvailableForHolderOnly()) {
      throw new AuthorisedUserIsHolderParamNeededForThisStatusException(action.name());
    }
    if (action.getOperationPermissionOnlyList() != null) {
      throw new PermittedOperationParamNeededForThisStatusException(action.name());
    }
    return nextState(schemaMap, action)
        .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
  }

  @Override
  public InvoiceStatus nextState(InvoiceActionTypeEnum action, Boolean authorisedUserIsHolder, InvoiceOperationPermission permittedOperation) {
    if (action.isAvailableForHolderOnly() && !authorisedUserIsHolder) {
      throw new InvoiceActionIsProhibitedForNotHolderException(String.format("current status: %s action: %s", this.name(), action.name()));
    }
    if (action.getOperationPermissionOnlyList() != null && !action.getOperationPermissionOnlyList().contains(permittedOperation)) {
      throw new InvoiceActionIsProhibitedForCurrencyPermissionOperationException(String.format("current status: %s action: %s permittedOperation: %s", this.name(), action.name(), permittedOperation.name()));
    }
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

  public Set<InvoiceActionTypeEnum> getAvailableActionList() {
    schemaMap.keySet().stream()
        .filter(InvoiceActionTypeEnum::isAvailableForHolderOnly)
        .findAny()
        .ifPresent(action -> {
          throw new AuthorisedUserIsHolderParamNeededForThisStatusException(action.name());
        });
    schemaMap.keySet().stream()
        .filter(e->e.getOperationPermissionOnlyList() != null)
        .findAny()
        .ifPresent(action -> {
          throw new PermittedOperationParamNeededForThisStatusException(action.name());
        });
    return schemaMap.keySet();
  }

  public Set<InvoiceActionTypeEnum> getAvailableActionList(Boolean authorisedUserIsHolder, InvoiceOperationPermission permittedOperation) {
    return schemaMap.keySet().stream()
        .filter(e -> (!e.isAvailableForHolderOnly() || authorisedUserIsHolder) &&
            (e.getOperationPermissionOnlyList()==null || e.getOperationPermissionOnlyList().contains(permittedOperation)))
        .collect(Collectors.toSet());
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
        .orElseThrow(() -> new UnsupportedInvoiceRequestStatusIdException(String.valueOf(id)));
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
    if (candidateList.size() == 0) {
      log.fatal("begin state not found");
      throw new AssertionError();
    }
    if (candidateList.size() > 1) {
      log.fatal("more than single begin state found: " + candidateList);
      throw new AssertionError();
    }
    return candidateList.get(0);
  }

  public static Set<InvoiceStatus> getMiddleStatesSet() {
    return Arrays.stream(InvoiceRequestStatusEnum.class.getEnumConstants())
        .filter(e -> !e.schemaMap.isEmpty())
        .collect(Collectors.toSet());
  }

  public static Set<InvoiceStatus> getEndStatesSet() {
    return Arrays.stream(InvoiceRequestStatusEnum.class.getEnumConstants())
        .filter(e -> e.schemaMap.isEmpty())
        .collect(Collectors.toSet());
  }

  public static InvoiceStatus getInvoiceStatusAfterAction(InvoiceActionTypeEnum action) {
    TreeSet<InvoiceStatus> statusSet = new TreeSet(
        Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
            .filter(e -> e.availableForAction(action))
            .map(e -> e.nextState(action))
            .collect(Collectors.toList()));
    if (statusSet.size()==0) {
      log.fatal("no state found !");
      throw new AssertionError();
    }
    if (statusSet.size()>1) {
      log.fatal("more then one state found !");
      throw new AssertionError();
    }
    return statusSet.first();
  }

  @Override
  public Boolean isEndStatus() {
    return schemaMap.isEmpty();
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

