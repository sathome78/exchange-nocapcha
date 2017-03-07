package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.InvoiceBank;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
public class InvoiceRequestFlatForReportDto {
  private int invoiceId;
  private String recipientBank;
  private String userFullName;
  private String payerBankCode;
  private InvoiceRequestStatusEnum status;
  private LocalDateTime acceptanceTime;

  private String userEmail;
  private String acceptanceUserEmail;

  private BigDecimal amount;
  private BigDecimal commissionAmount;
  private LocalDateTime datetime;

  private String currency;
}
