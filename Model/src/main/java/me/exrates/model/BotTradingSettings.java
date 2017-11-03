package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.PriceGrowthDirection;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class BotTradingSettings {
    private Integer id;
    private BotLaunchSettings botLaunchSettings;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private BigDecimal maxUserPrice;
    private BigDecimal minUserPrice;
    private BigDecimal priceStep;
    private PriceGrowthDirection direction;
}
