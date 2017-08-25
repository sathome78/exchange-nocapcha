package me.exrates.model.dto.merchants.lisk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class LiskSendTxDto {
    private String secret;
    private BigDecimal amount;
    private String recipientId;
}
