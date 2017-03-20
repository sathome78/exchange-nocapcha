package me.exrates.model.enums.invoice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ValkSam
 */
public enum InvoiceActionTypeButtonEnum {
  CONFIRM_USER_BUTTON,
  CONFIRM_ADMIN_BUTTON,
  REVOKE_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"inputoutput-table"});
  }},
  ACCEPT_BUTTON,
  DECLINE_BUTTON,
  DECLINE_HOLDED_BUTTON {{
    getProperty().put("availableForHolderOnly", true);
  }},
  POST_BUTTON,
  POST_HOLDED_BUTTON {{
    getProperty().put("availableForHolderOnly", true);
  }},
  TAKE_TO_WORK_BUTTON,
  RETURN_FROM_WORK_BUTTON {{
    getProperty().put("availableForHolderOnly", true);
  }};

  private Map<String, Object> property = new HashMap<>();

  InvoiceActionTypeButtonEnum() {
    property.put("buttonId", this.name().toLowerCase());
    property.put("buttonTitle", "action.button.".concat(this.name()));
    property.put("tableIdListOnly", new String[]{});
    property.put("availableForHolderOnly", false);
  }

  public Map<String, Object> getProperty() {
    return property;
  }
}
