package me.exrates.dao;

import me.exrates.model.BotLaunchSettings;
import me.exrates.model.BotTradingSettings;
import me.exrates.model.BotTrader;
import me.exrates.model.dto.BotTradingSettingsShortDto;
import me.exrates.model.enums.PriceGrowthDirection;

import java.util.List;
import java.util.Optional;

public interface BotDao {
    Optional<BotTrader> retrieveBotTrader();

    void createBot(Integer userId);

    void updateBot(BotTrader botTrader);

    Optional<BotTradingSettings> retrieveBotSettingsForCurrencyPairAndOrderType(int botId, int currencyPairId, int orderTypeId);

    BotLaunchSettings retrieveBotLaunchSettingsForCurrencyPair(int botId, int currencyPairId);

    void updatePriceGrowthDirection(int settingsId, PriceGrowthDirection direction);

    void setEnabledForCurrencyPair(int botId, int currencyPairId, boolean isEnabled);

    List<BotLaunchSettings> retrieveLaunchSettingsForAllPairs(int botId, Boolean isEnabled);

    BotTradingSettingsShortDto retrieveTradingSettingsShort(int botLaunchSettingsId, int orderTypeId);
}
