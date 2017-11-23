package me.exrates.service.neo;

import me.exrates.model.dto.merchants.neo.Block;
import me.exrates.model.dto.merchants.neo.NeoAsset;
import me.exrates.model.dto.merchants.neo.NeoTransaction;

import java.math.BigDecimal;
import java.util.Optional;

public interface NeoNodeService {
    String getNewAddress();

    Integer getBlockCount();

    Optional<Block> getBlock(Integer height);

    Optional<NeoTransaction> getTransactionById(String txId);

    NeoTransaction sendToAddress(NeoAsset asset, String address, BigDecimal amount, String changeAddress);
}
