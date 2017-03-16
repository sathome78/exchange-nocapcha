package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
public class WithdrawRequestFlatForReportDto {
  private int invoiceId;
  private String wallet;
  private String recipientBank;
  private String userFullName;
  private WithdrawalRequestStatus status;
  private WithdrawStatusEnum withdrawStatus;
  private LocalDateTime acceptanceTime;

  private String userEmail;
  private String acceptanceUserEmail;

  private BigDecimal amount;
  private BigDecimal commissionAmount;
  private LocalDateTime datetime;
  private String merchant;

  private String currency;
  private TransactionSourceType sourceType;
}
