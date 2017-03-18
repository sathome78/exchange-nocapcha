package me.exrates.model.enums.invoice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ValkSam
 */
public enum InvoiceActionTypeButtonEnum {
  CONFIRM_BUTTON,
  REVOKE_BUTTON,
  ACCEPT_BUTTON,
  DECLINE_BUTTON,
  POST_BUTTON,
  TAKE_TO_WORK_BUTTON,
  RETURN_FROM_WORK_BUTTON;

  private Map<String, String> property = new HashMap<>();

  InvoiceActionTypeButtonEnum() {
    property.put("buttonId", this.name().toLowerCase());
    property.put("buttonTitle", "action.button.".concat(this.name()));
  }

  public Map<String, String> getProperty() {
    return property;
  }
}
