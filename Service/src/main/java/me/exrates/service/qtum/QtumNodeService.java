package me.exrates.service.qtum;

import me.exrates.model.dto.merchants.qtum.Block;
import me.exrates.model.dto.merchants.qtum.QtumListTransactions;

import java.math.BigDecimal;
import java.util.Optional;

public interface QtumNodeService {
    String getNewAddress();

    String getBlockHash(Integer height);

    Block getBlock(String hash);

    Optional<QtumListTransactions> listSinceBlock(String blockHash);

    void setWalletPassphrase();

    BigDecimal getBalance();

    void transfer(String mainAddress, BigDecimal amount);

    void backupWallet();
}
