package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;

/**
 * Created by ValkSam
 */
@Getter @Setter
public class UserCurrencyOperationPermissionDto {
  private Integer userId;
  private Integer currencyId;
  private String currencyName;
  private InvoiceOperationDirection invoiceOperationDirection;
  private InvoiceOperationPermission invoiceOperationPermission;
}
