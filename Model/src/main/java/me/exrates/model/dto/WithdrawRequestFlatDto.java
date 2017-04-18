package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
public class WithdrawRequestFlatDto {
  private int id;
  private String wallet;
  private Integer userId;
  private Integer merchantImageId;
  private String recipientBankName;
  private String recipientBankCode;
  private String userFullName;
  private String remark;
  private BigDecimal amount;
  private BigDecimal commissionAmount;
  private BigDecimal netAmount;
  private Integer commissionId;
  private WithdrawStatusEnum status;
  private LocalDateTime dateCreation;
  private LocalDateTime statusModificationDate;
  private Integer currencyId;
  private Integer merchantId;
  private Integer adminHolderId;
  private InvoiceOperationPermission invoiceOperationPermission;
}
