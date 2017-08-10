package me.exrates.dao;

import me.exrates.model.BotTradingSettings;
import me.exrates.model.BotTrader;
import me.exrates.model.enums.PriceGrowthDirection;

import java.util.Optional;

public interface BotDao {
    Optional<BotTrader> retrieveBotTrader();

    void createBot(Integer userId);

    void updateBot(BotTrader botTrader);

    Optional<BotTradingSettings> retrieveBotSettingsForCurrencyPairAndOrderType(int botId, int currencyPairId, int orderTypeId);

    void updatePriceGrowthDirection(int settingsId, PriceGrowthDirection direction);

    void setEnabledForCurrencyPair(int botId, int currencyPairId, boolean isEnabled);
}
