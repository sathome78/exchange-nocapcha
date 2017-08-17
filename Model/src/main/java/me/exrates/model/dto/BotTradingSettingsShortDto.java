package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OrderType;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter @Setter
@ToString
public class BotTradingSettingsShortDto {
    private Integer id;
    private OrderType orderType;
    @Min(value = 0, message = "Create timeout value must be greater than 0")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal minAmount;
    @Min(value = 0, message = "Create timeout value must be greater than 0")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal maxAmount;
    @Min(value = 0, message = "Create timeout value must be greater than 0")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal minPrice;
    @Min(value = 0, message = "Create timeout value must be greater than 0")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal maxPrice;
    @Min(value = 0, message = "Price step value must be greater than 0")
    @NotNull(message = "{bot.notnull}")
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal priceStep;
}
