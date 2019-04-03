package me.exrates.model.dto.ieo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import me.exrates.model.IEODetails;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class IEODetailsUpdateDto {

    private int id;
    private String currencyName;
    private int makerId;
    private BigDecimal rate;
    private BigDecimal amount;
    private int contributors;
    private IEODetailsStatus status;
    private BigDecimal minAmount;
    private BigDecimal maxAmountPerClaim;
    private BigDecimal maxAmountPerUser;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    private int createdBy;
    private int version;
    private String currencyDescription;
    private String priceString;
    private BigDecimal availableBalance;
    private String currencyInPairName;

    public IEODetailsUpdateDto(IEODetails ieoDetails) {
        this.id = ieoDetails.getId();
        this.currencyName = ieoDetails.getCurrencyName();
        this.makerId = ieoDetails.getMakerId();
        this.rate = ieoDetails.getRate();
        this.amount = ieoDetails.getAmount();
        this.contributors = ieoDetails.getContributors();
        this.status = ieoDetails.getStatus();
        this.minAmount = ieoDetails.getMinAmount();
        this.maxAmountPerClaim = ieoDetails.getMaxAmountPerClaim();
        this.maxAmountPerUser = ieoDetails.getMaxAmountPerUser();
        this.startDate = ieoDetails.getStartDate();
        this.endDate = ieoDetails.getEndDate();
        this.createdAt = ieoDetails.getCreatedAt();
        this.createdBy = ieoDetails.getCreatedBy();
        this.version = ieoDetails.getVersion();
        this.currencyDescription = currencyName;
        this.priceString = priceString;
        this.availableBalance = ieoDetails.getAvailableAmount();
    }
}
