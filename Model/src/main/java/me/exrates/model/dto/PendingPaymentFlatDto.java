package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
public class PendingPaymentFlatDto {
  private int invoiceId;
  private String transactionHash;
  private String address;
  private PendingPaymentStatusEnum pendingPaymentStatus;
  private LocalDateTime statusUpdateDate;
  private LocalDateTime acceptanceTime;
  private String hash;

  private Integer userId;
  private String userEmail;
  private Integer acceptanceUserId;
  private String acceptanceUserEmail;

  private BigDecimal amount;
  private BigDecimal commissionAmount;
  private LocalDateTime datetime;
  private Integer confirmation;
  private Boolean provided;
}
