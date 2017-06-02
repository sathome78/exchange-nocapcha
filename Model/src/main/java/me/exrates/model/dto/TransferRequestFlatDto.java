package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
@ToString
public class TransferRequestFlatDto {
  private int id;
  private BigDecimal amount;
  private LocalDateTime dateCreation;
  private WithdrawStatusEnum status;
  private LocalDateTime statusModificationDate;
  private Integer merchantId;
  private Integer currencyId;
  private Integer userId;
  private Integer recipientId;
  private BigDecimal commissionAmount;
  private Integer commissionId;
  private String hash;
}
