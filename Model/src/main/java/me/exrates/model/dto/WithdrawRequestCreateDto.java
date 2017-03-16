package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.MerchantImage;
import me.exrates.model.enums.WithdrawalRequestStatus;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter @Setter
public class WithdrawRequestCreateDto {
    private Integer id;
    private Integer userId;
    private String userEmail;
    private Integer userWalletId;
    private BigDecimal amount;
    private BigDecimal commission;
    private String destinationWallet;
    private MerchantImage merchantImage;
    private Integer statusId;
    private String recipientBankName;
    private String recipientBankCode;
    private String userFullName;
    private String remark;

}
