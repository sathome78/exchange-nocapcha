package me.exrates.service.neo;

import me.exrates.model.dto.merchants.neo.Block;
import me.exrates.model.dto.merchants.neo.NeoAsset;
import me.exrates.model.dto.merchants.neo.NeoTransaction;

import java.math.BigDecimal;

public interface NeoNodeService {
    String getNewAddress();

    Integer getBlockCount();

    Block getBlock(Integer height);

    NeoTransaction getTransactionById(String txId);

    NeoTransaction sendToAddress(NeoAsset asset, String address, BigDecimal amount);
}
