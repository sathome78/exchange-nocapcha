package me.exrates.model.dto.merchants.btc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class BtcWalletPaymentItemDto {
    private String address;
    private BigDecimal amount;
}
