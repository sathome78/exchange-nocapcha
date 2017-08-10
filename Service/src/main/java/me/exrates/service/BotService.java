package me.exrates.service;

import me.exrates.model.BotTrader;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.enums.OrderType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BotService {
    void acceptAfterDelay(ExOrder exOrder);

    Optional<BotTrader> retrieveBotFromDB();

    void createBot(String nickname, String email, String password);

    void updateBot(BotTrader botTrader);

    @Transactional(readOnly = true)
    void runOrderCreation(Integer currencyPairId, OrderType orderType);

    void enableBotForCurrencyPair(CurrencyPair currencyPair);
}
