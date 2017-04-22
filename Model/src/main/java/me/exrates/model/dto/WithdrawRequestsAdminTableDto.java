package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.MerchantImage;
import me.exrates.model.dto.WithdrawRequestFlatAdditionalDataDto;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.dto.onlineTableDto.OnlineTableDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static me.exrates.model.enums.TransactionSourceType.WITHDRAW;

/**
 * Created by ValkSam
 */
@Getter @Setter
@ToString
public class WithdrawRequestsAdminTableDto extends OnlineTableDto {
  private Integer id;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime dateCreation;
  private Integer userId;
  private String userEmail;
  private BigDecimal amount;
  private String currencyName;
  private BigDecimal commissionAmount;
  private BigDecimal netAmount;
  private String merchantName;
  private String wallet;
  private Integer adminHolderId;
  private String adminHolderEmail;
  private String recipientBankName;
  private String recipientBankCode;
  private String userFullName;
  private String remark;
  private WithdrawStatusEnum status;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime statusModificationDate;
  private TransactionSourceType sourceType = WITHDRAW;
  private InvoiceOperationPermission invoiceOperationPermission;
  private Boolean isEndStatus;
  private List<Map<String, Object>> buttons;

  public WithdrawRequestsAdminTableDto(
      WithdrawRequestFlatDto withdrawRequestFlatDto,
      WithdrawRequestFlatAdditionalDataDto withdrawRequestFlatAdditionalDataDto) {
    this.id = withdrawRequestFlatDto.getId();
    this.dateCreation = withdrawRequestFlatDto.getDateCreation();
    this.userId = withdrawRequestFlatDto.getUserId();
    this.userEmail = withdrawRequestFlatAdditionalDataDto.getUserEmail();
    this.amount = withdrawRequestFlatDto.getAmount();
    this.currencyName = withdrawRequestFlatAdditionalDataDto.getCurrencyName();
    this.commissionAmount = withdrawRequestFlatDto.getCommissionAmount();
    this.netAmount = withdrawRequestFlatDto.getNetAmount();
    this.merchantName = withdrawRequestFlatAdditionalDataDto.getMerchantName();
    this.wallet = withdrawRequestFlatDto.getWallet();
    this.adminHolderId = withdrawRequestFlatDto.getAdminHolderId();
    this.adminHolderEmail = withdrawRequestFlatAdditionalDataDto.getAdminHolderEmail();
    this.recipientBankName = withdrawRequestFlatDto.getRecipientBankName();
    this.recipientBankCode = withdrawRequestFlatDto.getRecipientBankCode();
    this.userFullName = withdrawRequestFlatDto.getUserFullName();
    this.remark = withdrawRequestFlatDto.getRemark();
    this.status = withdrawRequestFlatDto.getStatus();
    this.statusModificationDate = withdrawRequestFlatDto.getStatusModificationDate();
    this.invoiceOperationPermission = withdrawRequestFlatDto.getInvoiceOperationPermission();
    this.isEndStatus = this.status.isEndStatus();
    this.buttons = null;
  }
}
