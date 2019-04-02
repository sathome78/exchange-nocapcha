package me.exrates.service.ieo.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.model.IEOClaim;
import me.exrates.service.WalletService;
import me.exrates.service.ieo.IEOQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log4j2
public class IEOQueueServiceImpl implements IEOQueueService {

    private final Queue<IEOClaim> claims;
    private final WalletService walletService;
    private final ExecutorService executor;
    private final IEOClaimRepository claimRepository;

    @Autowired
    public IEOQueueServiceImpl(IEOClaimRepository claimRepository, WalletService walletService) {
        this.claims = new ConcurrentLinkedQueue<>();
        this.executor = Executors.newSingleThreadExecutor();
        this.claimRepository = claimRepository;
        this.walletService = walletService;
    }

    @PostConstruct
    public void init() {
        Collection<IEOClaim> unprocessedIeoClaims = claimRepository.findUnprocessedIeoClaims();
        claims.addAll(unprocessedIeoClaims);
    }

    @Scheduled(fixedDelay = 20000)
    public void processClaims() {
        while (!claims.isEmpty()) {
            IEOClaim claim = claims.poll();
            if (claim != null) {
                executor.execute(new  );
            }
        }
    }

    @Override
    public boolean add(IEOClaim claim) {
        return claims.offer(claim);
    }

}
