package me.exrates.service.ieo.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.IEOResultRepository;
import me.exrates.model.IEOClaim;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
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
    private final IEOResultRepository ieoResultRepository;
    private final UserService userService;
    private final WalletService walletService;

    @Autowired
    public IEOQueueServiceImpl(IEOResultRepository ieoResultRepository,
                               UserService userService,
                               WalletService walletService) {
        this.ieoResultRepository = ieoResultRepository;
        this.walletService = walletService;
        this.claims = new ConcurrentLinkedQueue<>();
        this.userService = userService;
    }

    @PostConstruct
    public void init() {

    }

    @Override
    public boolean add(IEOClaim claim) {
        claim = walletService.blockUserBtcWalletWithIeoClaim(claim);
        return claims.offer(claim);
    }

}
