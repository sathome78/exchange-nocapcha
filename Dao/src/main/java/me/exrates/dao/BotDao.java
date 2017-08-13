package me.exrates.dao;

import me.exrates.model.BotTrader;

import java.util.Optional;

public interface BotDao {
    Optional<BotTrader> retrieveBotTrader();

    void createBot(Integer userId);

    void updateBot(BotTrader botTrader);
}
