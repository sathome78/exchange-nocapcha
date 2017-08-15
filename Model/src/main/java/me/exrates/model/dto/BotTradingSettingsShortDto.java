package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OrderType;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class BotTradingSettingsShortDto {
    private Integer id;
    private OrderType orderType;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal priceStep;
}
