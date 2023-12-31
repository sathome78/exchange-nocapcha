package me.exrates.model.dto.freecoins;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class GiveawayResultDto {

    private int id;
    @JsonProperty("currency_name")
    private String currencyName;
    @JsonProperty("currency_description")
    private String currencyDescription;
    private BigDecimal amount;
    @JsonProperty("partial_amount")
    private BigDecimal partialAmount;
    @JsonProperty("total_quantity")
    private int totalQuantity;
    @JsonProperty("single")
    private boolean isSingle;
    @JsonProperty("time_range")
    private Integer timeRange;
    @JsonIgnore
    private GiveawayStatus status;
    @JsonIgnore
    private String creatorEmail;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
