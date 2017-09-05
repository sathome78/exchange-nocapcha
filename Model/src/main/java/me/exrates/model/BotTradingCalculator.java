package me.exrates.model;

import lombok.Getter;
import lombok.ToString;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PriceGrowthDirection;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static me.exrates.model.enums.ActionType.*;
import static me.exrates.model.enums.PriceGrowthDirection.*;

@Getter
@ToString
public class BotTradingCalculator {
    private final Integer id;
    private final BotLaunchSettings botLaunchSettings;
    private final OrderType orderType;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final BigDecimal lowerPriceBound;
    private final BigDecimal upperPriceBound;
    private final BigDecimal priceStep;
    private PriceGrowthDirection direction;

    public BotTradingCalculator(Integer id, BotLaunchSettings botLaunchSettings, OrderType orderType, BigDecimal minAmount, BigDecimal maxAmount,
                                BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minUserPrice, BigDecimal maxUserPrice, BigDecimal priceStep,
                                PriceGrowthDirection direction) {
        this.id = id;
        this.botLaunchSettings = botLaunchSettings;
        this.orderType = orderType;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.priceStep = priceStep;
        this.direction = direction;

        if (botLaunchSettings.isUserOrderPriceConsidered()) {
            if (minUserPrice == null || !checkPriceWithinRange(minUserPrice, minPrice, maxPrice)) {
                lowerPriceBound = minPrice;
            } else {
                lowerPriceBound = minUserPrice;
            }

            BigDecimal lowerWithStep = lowerPriceBound.add(priceStep);

            if (maxUserPrice == null || !checkPriceWithinRange(maxUserPrice, minPrice, maxPrice)) {
                upperPriceBound = maxPrice;
            } else if (maxUserPrice.compareTo(lowerWithStep) < 0 && checkPriceWithinRange(lowerWithStep, minPrice, maxPrice)) {
                upperPriceBound = lowerPriceBound.add(priceStep);
            } else {
                upperPriceBound = maxUserPrice;
            }
        } else {
            lowerPriceBound = minPrice;
            upperPriceBound = maxPrice;
        }
    }

    private boolean checkPriceWithinRange(BigDecimal price, BigDecimal min, BigDecimal max) {
        return price.compareTo(min) >= 0 && price.compareTo(max) <= 0;
    }

    public BigDecimal getRandomizedAmount() {
        return new BigDecimal(new RandomDataGenerator().nextUniform(minAmount.doubleValue(), maxAmount.doubleValue())).setScale(5, RoundingMode.DOWN);
    }


    public BigDecimal nextPrice(final BigDecimal previousPrice) {
        BigDecimal result = calculateNextPrice(previousPrice);
        if (result.compareTo(lowerPriceBound) < 0) {
            result = lowerPriceBound;
            direction = UP;
        } else if(result.compareTo(upperPriceBound) > 0) {
            result = upperPriceBound;
            direction = DOWN;
        }
        return result;
    }

    private BigDecimal calculateNextPrice(BigDecimal previousPrice) {
        ActionType actionType = direction == UP ? ADD : SUBTRACT;
        return BigDecimalProcessing.doAction(previousPrice, priceStep, actionType);
    }

    public void setDirection(PriceGrowthDirection direction) {
        this.direction = direction;
    }
}
