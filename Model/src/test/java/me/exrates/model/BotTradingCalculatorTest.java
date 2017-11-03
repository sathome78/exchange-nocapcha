package me.exrates.model;

import me.exrates.model.enums.PriceGrowthDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.math.BigDecimal;

public class BotTradingCalculatorTest {

    private BotTradingSettings settings;

    private BotTradingCalculator calculator;

    @Before
    public void setUp() {
        BotLaunchSettings botLaunchSettings = new BotLaunchSettings();
        botLaunchSettings.setUserOrderPriceConsidered(true);

        settings = new BotTradingSettings();
        settings.setId(10);
        settings.setBotLaunchSettings(botLaunchSettings);
        settings.setMaxAmount(new BigDecimal(500));
        settings.setMinAmount(new BigDecimal(100));
        settings.setMinPrice(new BigDecimal(1200));
        settings.setMaxPrice(new BigDecimal(1500));
        settings.setMinUserPrice(new BigDecimal(1250));
        settings.setMaxUserPrice(new BigDecimal(1450));
        settings.setPriceStep(new BigDecimal(10));
        settings.setDirection(PriceGrowthDirection.UP);

        calculator = new BotTradingCalculator(settings);

    }

    @Test
    public void initCalculatorStandardTest() {
        calculator = new BotTradingCalculator(settings);
        assertEquals(settings.getMinUserPrice(), calculator.getLowerPriceBound());
        assertEquals(settings.getMaxUserPrice(), calculator.getUpperPriceBound());
    }

    @Test
    public void initCalculator_UserBoundsExceedLimitsTest() {
        settings.setMinUserPrice(new BigDecimal(1150));
        settings.setMaxUserPrice(new BigDecimal(1550));
        calculator = new BotTradingCalculator(settings);
        assertEquals(settings.getMinPrice(), calculator.getLowerPriceBound());
        assertEquals(settings.getMaxPrice(), calculator.getUpperPriceBound());
    }

    @Test
    public void nextPriceStandardTest() {
        assertEquals(new BigDecimal(1310), calculator.nextPrice(new BigDecimal(1300)));
        assertEquals(PriceGrowthDirection.UP, calculator.getDirection());
    }

    @Test
    public void nextPriceStandardDownTest() {
        settings.setDirection(PriceGrowthDirection.DOWN);
        calculator = new BotTradingCalculator(settings);
        assertEquals(new BigDecimal(1290), calculator.nextPrice(new BigDecimal(1300)));
        assertEquals(PriceGrowthDirection.DOWN, calculator.getDirection());
    }

    @Test
    public void nextPrice_PreviousLowerTest() {
        assertEquals(new BigDecimal(1255), calculator.nextPrice(new BigDecimal(1245)));
        assertEquals(PriceGrowthDirection.UP, calculator.getDirection());
    }

    @Test
    public void nextPrice_PreviousSignificantlyLowerTest() {
        assertEquals(new BigDecimal(1250), calculator.nextPrice(new BigDecimal(1145)));
        assertEquals(PriceGrowthDirection.UP, calculator.getDirection());
    }

    @Test
    public void nextPrice_NextLowerTest() {
        settings.setDirection(PriceGrowthDirection.DOWN);
        calculator = new BotTradingCalculator(settings);
        assertEquals(new BigDecimal(1250), calculator.nextPrice(new BigDecimal(1255)));
        assertEquals(PriceGrowthDirection.UP, calculator.getDirection());
    }



    @Test
    public void nextPrice_PreviousHigherTest() {
        assertEquals(new BigDecimal(1450), calculator.nextPrice(new BigDecimal(1600)));
        assertEquals(PriceGrowthDirection.DOWN, calculator.getDirection());
    }

    @Test
    public void nextPrice_NextHigherTest() {
        assertEquals(new BigDecimal(1450), calculator.nextPrice(new BigDecimal(1445)));
        assertEquals(PriceGrowthDirection.DOWN, calculator.getDirection());
    }

    @Test
    public void nextPrice_PreviousEqualToHigherBoundTest() {
        assertEquals(new BigDecimal(1440), calculator.nextPrice(new BigDecimal(1450)));
        assertEquals(PriceGrowthDirection.DOWN, calculator.getDirection());
    }

    @Test
    public void nextPrice_PreviousEqualToLowerBoundTest() {
        settings.setDirection(PriceGrowthDirection.DOWN);
        calculator = new BotTradingCalculator(settings);
        assertEquals(new BigDecimal(1260), calculator.nextPrice(new BigDecimal(1250)));
        assertEquals(PriceGrowthDirection.UP, calculator.getDirection());
    }









}
