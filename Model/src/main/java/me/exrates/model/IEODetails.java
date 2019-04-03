package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.model.serializer.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class IEODetails {

    @JsonIgnore
    private int id;
    private String currencyName;
    @JsonIgnore
    private int makerId;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal availableAmount;
    private int contributors;
    private IEODetailsStatus status;
    private BigDecimal minAmount;
    private BigDecimal maxAmountPerClaim;
    @JsonIgnore
    private BigDecimal maxAmountPerUser;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDateTime startDate;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDateTime endDate;
    @JsonIgnore
    private LocalDateTime createdAt;
    @JsonIgnore
    private int createdBy;
    private int version;

    public IEODetails(int id, String currencyName, int makerId, BigDecimal rate, BigDecimal amount, int contributors,
                      IEODetailsStatus status, BigDecimal minAmount, BigDecimal maxAmountPerClaim, BigDecimal maxAmountPerUser,
                      LocalDateTime startDate, LocalDateTime endDate, int version) {
        this.id = id;
        this.currencyName = currencyName;
        this.makerId = makerId;
        this.rate = rate;
        this.amount = amount;
        this.availableAmount = amount;
        this.contributors = contributors;
        this.status = status;
        this.minAmount = minAmount;
        this.maxAmountPerClaim = maxAmountPerClaim;
        this.maxAmountPerUser = maxAmountPerUser;
        this.startDate = startDate;
        this.endDate = endDate;
        this.version = version;
    }
}
