package me.exrates.service.ieo.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.IEOClaim;
import me.exrates.model.Wallet;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.IeoException;
import me.exrates.service.ieo.IEOQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Log4j2
public class IEOQueueServiceImpl implements IEOQueueService {

    private final Queue<IEOClaim> claims;
    private final UserService userService;
    private final WalletService walletService;

    @Autowired
    public IEOQueueServiceImpl(UserService userService,
                               WalletService walletService) {
        this.walletService = walletService;
        this.claims = new ConcurrentLinkedQueue<>();
        this.userService = userService;
    }

    @PostConstruct
    public void init() {

    }

    @Override
    public boolean add(IEOClaim claim) {
        Wallet userWallet = walletService.findByUserAndCurrency(claim.getUserId(), "BTC");
        if (userWallet == null || userWallet.getActiveBalance().compareTo(claim.getPriceInBtc()) > 0) {
            // user has insufficient funds
            String message = String.format("Failed to apply as user has insufficient funds: suggested %s BTC, but available is %s BTC",
                    claim.getPriceInBtc().toPlainString(), userWallet == null ? "0" : userWallet.getActiveBalance().toPlainString());
            log.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_INSUFFICIENT_BUYER_FUNDS, message);
        }
        return false;
    }
}
