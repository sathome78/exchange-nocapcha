package me.exrates.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Builder
@Getter @Setter
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
  private boolean toMainAccountTransferringNeeded = false;
}
