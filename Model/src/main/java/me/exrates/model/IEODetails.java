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

}
