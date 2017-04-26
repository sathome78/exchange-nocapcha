package me.exrates.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.CreditsOperation;
import me.exrates.model.enums.invoice.RefillStatusEnum;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

/**
 * @author ValkSam
 */
@Builder
@Getter
@ToString
public class RefillRequestAcceptDto {
  private Integer requestId;
  private Integer merchantId;
  private Integer currencyId;
  private BigDecimal amount;
  private String address;
  private String merchantTransactionId;
  private Integer requesterAdminId;
  private String remark;
}
