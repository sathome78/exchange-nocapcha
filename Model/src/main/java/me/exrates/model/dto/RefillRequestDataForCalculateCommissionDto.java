package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.RefillStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
@AllArgsConstructor
public class RefillRequestDataForCalculateCommissionDto {
  private Integer userId;
  private Integer currencyId;
  private Integer merchantId;
  private Integer commissionId;
  private BigDecimal amount;
}
