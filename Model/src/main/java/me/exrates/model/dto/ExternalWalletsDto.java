package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ExternalWalletsDto {

    private Integer currencyId;
    private Integer merchantId;
    private String currencyName;
    private BigDecimal reservedWalletBalance;
    private BigDecimal coldWalletBalance;
    private BigDecimal totalReal;
    private BigDecimal mainWalletBalance = BigDecimal.ZERO;

}
