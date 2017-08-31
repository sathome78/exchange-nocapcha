package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PriceGrowthDirection;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.commons.math3.random.RandomDataGenerator;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static me.exrates.model.enums.ActionType.*;
import static me.exrates.model.enums.PriceGrowthDirection.*;

@Getter @Setter
@ToString
public class BotTradingSettings {
    private Integer id;
    private BotLaunchSettings botLaunchSettings;
    private OrderType orderType;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal priceStep;
    private PriceGrowthDirection direction;


    public BigDecimal getRandomizedAmount() {
        return new BigDecimal(new RandomDataGenerator().nextUniform(minAmount.doubleValue(), maxAmount.doubleValue())).setScale(5, RoundingMode.DOWN);
    }


    public BigDecimal nextPrice(final BigDecimal previousPrice) {
        BigDecimal result = calculateNextPrice(previousPrice);
        if (result.compareTo(minPrice) < 0) {
            result = minPrice;
            direction = UP;
        } else if(result.compareTo(maxPrice) > 0) {
            result = maxPrice;
            direction = DOWN;
        }
        return result;
    }

    private BigDecimal calculateNextPrice(BigDecimal previousPrice) {
        ActionType actionType = direction == UP ? ADD : SUBTRACT;
        return BigDecimalProcessing.doAction(previousPrice, priceStep, actionType);
    }

}
