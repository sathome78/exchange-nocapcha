package me.exrates.model.enums.invoice;

import lombok.NoArgsConstructor;
import me.exrates.model.exceptions.NoButtonPropertyForActionTypeException;
import me.exrates.model.exceptions.UnsupportedInvoiceActionTypeNameException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.*;
import static me.exrates.model.enums.invoice.InvoiceOperationPermission.ACCEPT_DECLINE;

/**
 * Created by ValkSam on 18.02.2017.
 */
@NoArgsConstructor
public enum InvoiceActionTypeEnum {
  CONFIRM_USER(CONFIRM_USER_BUTTON),
  CONFIRM_ADMIN(CONFIRM_ADMIN_BUTTON, false, ACCEPT_DECLINE),
  REVOKE(REVOKE_BUTTON),
  EXPIRE,
  BCH_EXAMINE,
  ACCEPT_MANUAL(ACCEPT_BUTTON, false, ACCEPT_DECLINE),
  ACCEPT_AUTO,
  DECLINE(DECLINE_BUTTON, false, ACCEPT_DECLINE),
  DECLINE_HOLDED(DECLINE_HOLDED_BUTTON, true, ACCEPT_DECLINE),
  PUT_FOR_MANUAL,
  PUT_FOR_AUTO,
  PUT_FOR_CONFIRM,
  POST,
  POST_HOLDED(POST_HOLDED_BUTTON, true, ACCEPT_DECLINE),
  TAKE_TO_WORK(TAKE_TO_WORK_BUTTON, false, ACCEPT_DECLINE),
  RETURN_FROM_WORK(RETURN_FROM_WORK_BUTTON, true, ACCEPT_DECLINE);

  private InvoiceActionTypeButtonEnum actionTypeButton = null;
  private Boolean availableForHolderOnly = false;
  private List<InvoiceOperationPermission> operationPermissionOnlyList = null;

  InvoiceActionTypeEnum(InvoiceActionTypeButtonEnum actionTypeButton) {
    this.actionTypeButton = actionTypeButton;
  }

  InvoiceActionTypeEnum(InvoiceActionTypeButtonEnum actionTypeButton, Boolean availableForHolderOnly, InvoiceOperationPermission ... operationPermissions) {
    this(actionTypeButton);
    this.availableForHolderOnly = availableForHolderOnly;
    this.operationPermissionOnlyList = Arrays.asList(operationPermissions);
  }

  public InvoiceActionTypeButtonEnum getActionTypeButton() {
    return actionTypeButton;
  }

  public Boolean isAvailableForHolderOnly() {
    return availableForHolderOnly;
  }

  public List<InvoiceOperationPermission> getOperationPermissionOnlyList() {
    return operationPermissionOnlyList;
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
