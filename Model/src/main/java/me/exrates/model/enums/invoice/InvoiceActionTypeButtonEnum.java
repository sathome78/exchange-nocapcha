package me.exrates.model.enums.invoice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ValkSam
 */
public enum InvoiceActionTypeButtonEnum {
  CONFIRM_USER_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"inputoutput-table"});
  }},
  CONFIRM_ADMIN_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
  }},
  REVOKE_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"inputoutput-table"});
  }},
  ACCEPT_BUTTON,
  DECLINE_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
  }},
  DECLINE_HOLDED_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
  }},
  POST_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
  }},
  POST_HOLDED_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
  }},
  TAKE_TO_WORK_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
  }},
  RETURN_FROM_WORK_BUTTON {{
    getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
  }};

  private Map<String, Object> property = new HashMap<>();

  InvoiceActionTypeButtonEnum() {
    property.put("buttonId", this.name().toLowerCase());
    property.put("buttonTitle", "action.button.".concat(this.name()));
    property.put("tableIdListOnly", new String[]{});
  }

  public Map<String, Object> getProperty() {
    return property;
  }
}
