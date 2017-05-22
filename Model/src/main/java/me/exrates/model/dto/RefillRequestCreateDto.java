package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.CreditsOperation;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * @author ValkSam
 */
@Getter @Setter
@ToString
@NoArgsConstructor
public class RefillRequestCreateDto {
  private Integer id;
  private Integer userId;
  private String userEmail;
  private Integer userWalletId;
  private Integer currencyId;
  private String currencyName;
  private BigDecimal amount;
  private BigDecimal commission;
  private Integer commissionId;
  private Integer merchantId;
  private String merchantDescription;
  private String serviceBeanName;
  private Integer refillOperationCountLimitForUserPerDay;
  private RefillStatusEnum status;
  private Integer recipientBankId;
  private String recipientBankCode;
  private String recipientBankName;
  private String recipient;
  private String userFullName;
  private String remark;
  private String address;
  private String privKey;
  private String pubKey;
  private String brainPrivKey;
  private Boolean generateNewAddress;
  private Boolean generateAdditionalRefillAddressAvailable;
  private Boolean needToCreateRefillRequestRecord;
  private Locale locale;

  public RefillRequestCreateDto(RefillRequestParamsDto paramsDto,  CreditsOperation creditsOperation, RefillStatusEnum status, Locale locale) {
    this.currencyId = paramsDto.getCurrency();
    this.amount = paramsDto.getSum();
    this.merchantId = paramsDto.getMerchant();
    this.recipientBankId = paramsDto.getRecipientBankId();
    this.recipientBankCode = paramsDto.getRecipientBankCode();
    this.recipientBankName = paramsDto.getRecipientBankName();
    this.recipient = paramsDto.getRecipient();
    this.userFullName = paramsDto.getUserFullName();
    this.remark = paramsDto.getRemark();
    this.address = paramsDto.getAddress();
    this.needToCreateRefillRequestRecord = !StringUtils.isEmpty(this.address);
    this.privKey = null;
    this.pubKey = null;
    this.brainPrivKey = null;
    this.generateNewAddress = paramsDto.getGenerateNewAddress();
    /**/
    this.userId = creditsOperation.getUser().getId();
    this.userEmail = creditsOperation.getUser().getEmail();
    this.userWalletId = creditsOperation.getWallet().getId();
    this.currencyName = creditsOperation.getCurrency().getName();
    this.commission = creditsOperation.getCommissionAmount();
    this.commissionId = creditsOperation.getCommission().getId();
    this.refillOperationCountLimitForUserPerDay = creditsOperation.getMerchant().getRefillOperationCountLimitForUserPerDay();
    this.serviceBeanName = creditsOperation.getMerchant().getServiceBeanName();
    this.merchantDescription = creditsOperation.getMerchant().getDescription();
    this.generateAdditionalRefillAddressAvailable = creditsOperation.getGenerateAdditionalRefillAddressAvailable();
    /**/
    this.status = status;
    /**/
    this.locale = locale;
  }
}
