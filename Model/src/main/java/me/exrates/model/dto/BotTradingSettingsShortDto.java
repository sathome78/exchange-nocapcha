package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OrderType;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class BotTradingSettingsShortDto {
    private Integer id;
    private OrderType orderType;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal minAmount;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal maxAmount;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal minPrice;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal maxPrice;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal priceStep;
}
