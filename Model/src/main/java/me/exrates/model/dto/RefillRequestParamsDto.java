package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter @Setter
@ToString
public class RefillRequestParamsDto {
    private OperationType operationType;
    private Integer currency;
    private BigDecimal sum;
    private Integer merchant;
    private Integer recipientBankId;
    private String recipientBankCode;
    private String recipientBankName;
    private String recipient;
    private String userFullName;
    private String remark;
    private String address;
}
