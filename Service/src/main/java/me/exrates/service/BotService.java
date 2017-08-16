package me.exrates.service;

import me.exrates.model.BotTrader;
import me.exrates.model.ExOrder;

import java.util.Optional;

public interface BotService {
    void acceptAfterDelay(ExOrder exOrder);

    Optional<BotTrader> retrieveBotFromDB();

    void createBot(String nickname, String email, String password);

    void updateBot(BotTrader botTrader);
}
