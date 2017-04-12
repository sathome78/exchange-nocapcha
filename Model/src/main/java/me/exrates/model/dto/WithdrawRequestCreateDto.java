package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.CreditsOperation;
import me.exrates.model.MerchantImage;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter @Setter
@ToString
public class WithdrawRequestCreateDto {
    private Integer id;
    private Integer userId;
    private String userEmail;
    private Integer userWalletId;
    private Integer currencyId;
    private String currencyName;
    private BigDecimal amount;
    private BigDecimal commission;
    private Integer commissionId;
    private String destinationWallet;
    private Integer merchantId;
    private String merchantDescription;
    private MerchantImage merchantImage;
    private Integer statusId;
    private String recipientBankName;
    private String recipientBankCode;
    private String userFullName;
    private String remark;
    private Boolean autoEnabled;
    private BigDecimal autoThresholdAmount;

    public WithdrawRequestCreateDto(WithdrawRequestParamsDto withdrawRequestParamsDto, CreditsOperation creditsOperation, WithdrawStatusEnum status) {
        this.userId = creditsOperation.getUser().getId();
        this.userEmail = creditsOperation.getUser().getEmail();
        this.userWalletId = creditsOperation.getWallet().getId();
        this.currencyId = creditsOperation.getCurrency().getId();
        this.currencyName = creditsOperation.getCurrency().getName();
        this.amount = creditsOperation.getOrigAmountAtCreationRequest();
        this.commission = creditsOperation.getCommissionAmount();
        this.commissionId = creditsOperation.getCommission().getId();
        if (creditsOperation.getDestination().isPresent() && !creditsOperation.getDestination().get().isEmpty()) {
            this.destinationWallet = creditsOperation.getDestination().get();
        } else {
            this.destinationWallet =  withdrawRequestParamsDto.getUserAccount();
        }
        this.merchantId = creditsOperation.getMerchant().getId();
        this.merchantDescription = creditsOperation.getMerchant().getDescription();
        this.merchantImage = creditsOperation.getMerchantImage().orElse(null);
        this.statusId = status.getCode();
        this.recipientBankName = withdrawRequestParamsDto.getRecipientBankName();
        this.recipientBankCode = withdrawRequestParamsDto.getRecipientBankCode();
        this.userFullName = withdrawRequestParamsDto.getUserFullName();
        this.remark = withdrawRequestParamsDto.getRemark();
        this.autoEnabled = null;
        this.autoThresholdAmount = null;
    }
}
