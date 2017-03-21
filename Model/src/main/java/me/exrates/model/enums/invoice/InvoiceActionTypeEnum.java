package me.exrates.model.enums.invoice;

import lombok.NoArgsConstructor;
import me.exrates.model.exceptions.NoButtonPropertyForActionTypeException;
import me.exrates.model.exceptions.UnsupportedInvoiceActionTypeNameException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceActionTypeButtonEnum.*;

/**
 * Created by ValkSam on 18.02.2017.
 */
@NoArgsConstructor
public enum InvoiceActionTypeEnum {
  CONFIRM_USER(CONFIRM_USER_BUTTON),
  CONFIRM_ADMIN(CONFIRM_ADMIN_BUTTON),
  REVOKE(REVOKE_BUTTON),
  EXPIRE,
  BCH_EXAMINE,
  ACCEPT_MANUAL(ACCEPT_BUTTON),
  ACCEPT_AUTO,
  DECLINE(DECLINE_BUTTON),
  DECLINE_HOLDED(DECLINE_HOLDED_BUTTON, true),
  PUT_FOR_MANUAL,
  PUT_FOR_AUTO,
  PUT_FOR_CONFIRM,
  POST(POST_BUTTON),
  POST_HOLDED(POST_HOLDED_BUTTON, true),
  TAKE_TO_WORK(TAKE_TO_WORK_BUTTON),
  RETURN_FROM_WORK(RETURN_FROM_WORK_BUTTON, true);

  private InvoiceActionTypeButtonEnum actionTypeButton = null;
  private Boolean availableForHolderOnly = false;

  InvoiceActionTypeEnum(InvoiceActionTypeButtonEnum actionTypeButton) {
    this(actionTypeButton, false);
  }

  InvoiceActionTypeEnum(InvoiceActionTypeButtonEnum actionTypeButton, Boolean availableForHolderOnly) {
    this.actionTypeButton = actionTypeButton;
    this.availableForHolderOnly = availableForHolderOnly;
  }

  public InvoiceActionTypeButtonEnum getActionTypeButton() {
    return actionTypeButton;
  }

  public Boolean isAvailableForHolderOnly() {
    return availableForHolderOnly;
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
