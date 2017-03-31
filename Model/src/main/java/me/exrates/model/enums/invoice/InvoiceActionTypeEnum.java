package me.exrates.model.enums.invoice;

import lombok.NoArgsConstructor;
import me.exrates.model.exceptions.UnsupportedInvoiceActionTypeNameException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.*;
import static me.exrates.model.enums.invoice.InvoiceOperationPermission.ACCEPT_DECLINE;

/**
 * Created by ValkSam on 18.02.2017.
 */
@NoArgsConstructor
public enum InvoiceActionTypeEnum {
  CONFIRM_USER {{
    getProperty().put("actionTypeButton", CONFIRM_USER_BUTTON);
  }},
  CONFIRM_ADMIN {{
    getProperty().put("actionTypeButton", CONFIRM_ADMIN_BUTTON);
    getProperty().put("operationPermissionOnlyList", Arrays.asList(ACCEPT_DECLINE));
  }},
  REVOKE {{
    getProperty().put("actionTypeButton", REVOKE_BUTTON);
  }},
  EXPIRE,
  BCH_EXAMINE,
  ACCEPT_MANUAL {{
    getProperty().put("actionTypeButton", ACCEPT_BUTTON);
    getProperty().put("operationPermissionOnlyList", Arrays.asList(ACCEPT_DECLINE));
    getProperty().put("leadsToSuccessFinalState", true);
  }},
  ACCEPT_AUTO{{
    getProperty().put("leadsToSuccessFinalState", true);
  }},
  DECLINE {{
    getProperty().put("actionTypeButton", DECLINE_BUTTON);
    getProperty().put("operationPermissionOnlyList", Arrays.asList(ACCEPT_DECLINE));
  }},
  DECLINE_HOLDED {{
    getProperty().put("actionTypeButton", DECLINE_HOLDED_BUTTON);
    getProperty().put("availableForHolderOnly", true);
    getProperty().put("operationPermissionOnlyList", Arrays.asList(ACCEPT_DECLINE));
  }},
  PUT_FOR_MANUAL,
  PUT_FOR_AUTO,
  PUT_FOR_CONFIRM,
  HOLD_TO_POST,
  POST_AUTO {{
    getProperty().put("leadsToSuccessFinalState", true);
  }},
  POST_HOLDED {{
    getProperty().put("actionTypeButton", POST_HOLDED_BUTTON);
    getProperty().put("availableForHolderOnly", true);
    getProperty().put("operationPermissionOnlyList", Arrays.asList(ACCEPT_DECLINE));
    getProperty().put("leadsToSuccessFinalState", true);
  }},
  TAKE_TO_WORK {{
    getProperty().put("actionTypeButton", TAKE_TO_WORK_BUTTON);
    getProperty().put("operationPermissionOnlyList", Arrays.asList(ACCEPT_DECLINE));
  }},
  RETURN_FROM_WORK {{
    getProperty().put("actionTypeButton", RETURN_FROM_WORK_BUTTON);
    getProperty().put("availableForHolderOnly", true);
    getProperty().put("operationPermissionOnlyList", Arrays.asList(ACCEPT_DECLINE));
  }};

  private Map<String, Object> property = new HashMap<String, Object>() {{
    put("actionTypeButton", null);
    put("availableForHolderOnly", false);
    put("operationPermissionOnlyList", null);
    put("leadsToSuccessFinalState", false);
  }};

  public Map<String, Object> getProperty() {
    return property;
  }

  public InvoiceActionTypeButtonEnum getActionTypeButton() {
    return (InvoiceActionTypeButtonEnum) property.get("actionTypeButton");
  }

  public Boolean isAvailableForHolderOnly() {
    return (Boolean) property.get("availableForHolderOnly");
  }

  public List<InvoiceOperationPermission> getOperationPermissionOnlyList() {
    return (List<InvoiceOperationPermission>) property.get("operationPermissionOnlyList");
  }

  public Boolean isLeadsToSuccessFinalState() {
    return (Boolean) property.get("leadsToSuccessFinalState");
  }

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
