package me.exrates.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Data
public class IEOClaim {
    private int id;
    private String currencyName;
    private int makerId;
    private int userId;
    private BigDecimal amount;
    private BigDecimal priceInBtc;
    private Date created;
    private IEOClaimStateEnum state;

    public enum IEOClaimStateEnum {
        created, processed
    }

    public IEOClaim(String currencyName, int makerId, int userId, BigDecimal amount, BigDecimal priceInBtc) {
        this.currencyName = currencyName;
        this.makerId = makerId;
        this.userId = userId;
        this.amount = amount;
        this.created = new Date();
        this.state = IEOClaimStateEnum.created;
        this.priceInBtc = priceInBtc;
    }
}
