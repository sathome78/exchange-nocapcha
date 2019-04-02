package me.exrates.service.ieo.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.IEOResultRepository;
import me.exrates.model.IEOClaim;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.ieo.IEOQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Log4j2
public class IEOQueueServiceImpl implements IEOQueueService {

    private final Queue<IEOClaim> claims;
    private final WalletService walletService;

    @Autowired
    public IEOQueueServiceImpl(WalletService walletService) {
        this.claims = new ConcurrentLinkedQueue<>();
        this.walletService = walletService;
    }

    @PostConstruct
    public void init() {

    }

    @Scheduled(fixedDelay = 20000)
    public void processClaims() {
        while (!claims.isEmpty()) {
            IEOClaim claim = claims.poll();
            if (claim != null) {

            }
        }
    }

    @Override
    public boolean add(IEOClaim claim) {
        claim = walletService.blockUserBtcWalletWithIeoClaim(claim);
        return claims.offer(claim);
    }

}
