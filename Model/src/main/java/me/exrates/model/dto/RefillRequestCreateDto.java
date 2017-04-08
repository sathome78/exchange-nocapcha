package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.CreditsOperation;
import me.exrates.model.enums.invoice.RefillStatusEnum;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter @Setter
@ToString
public class RefillRequestCreateDto {
  private Integer id;
  private Integer userId;
  private String userEmail;
  private Integer userWalletId;
  private Integer currencyId;
  private String currencyName;
  private BigDecimal amount;
  private BigDecimal commission;
  private BigDecimal amountWithCommission;
  private Integer commissionId;
  private Integer merchantId;
  private String serviceBeanName;
  private Integer refillOperationCountLimitForUserPerDay;
  private RefillStatusEnum status;
  private String recipientBankCode;
  private String recipientBankName;
  private String userFullName;
  private String remark;
  private String address;

  public RefillRequestCreateDto(RefillRequestParamsDto paramsDto,  CreditsOperation creditsOperation, RefillStatusEnum status) {
    this.currencyId = paramsDto.getCurrency();
    this.amount = paramsDto.getSum();
    this.merchantId = paramsDto.getMerchant();
    this.recipientBankCode = paramsDto.getRecipientBankCode();
    this.recipientBankName = paramsDto.getRecipientBankName();
    this.userFullName = paramsDto.getUserFullName();
    this.remark = paramsDto.getRemark();
    this.address = paramsDto.getAddress();
    /**/
    this.userId = creditsOperation.getUser().getId();
    this.userEmail = creditsOperation.getUser().getEmail();
    this.userWalletId = creditsOperation.getWallet().getId();
    this.currencyName = creditsOperation.getCurrency().getName();
    this.commission = creditsOperation.getCommissionAmount();
    this.commissionId = creditsOperation.getCommission().getId();
    this.refillOperationCountLimitForUserPerDay = creditsOperation.getMerchant().getRefillOperationCountLimitForUserPerDay();
    this.serviceBeanName = creditsOperation.getMerchant().getServiceBeanName();
    /**/
    this.amountWithCommission = this.amount.add(this.commission);
    this.status = status;
  }
}
