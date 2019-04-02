package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IEOInfoRepository;
import me.exrates.dao.IEOResultRepository;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEOInfo;
import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.model.dto.ieo.IEOStatusInfo;
import me.exrates.service.CurrencyService;
import me.exrates.service.IEOService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.IeoException;
import me.exrates.service.ieo.IEOQueueService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class IEOServiceImpl implements IEOService {
    private static final Logger LOGGER = LogManager.getLogger(IEOServiceImpl.class);

    private final IEOClaimRepository ieoClaimRepository;
    private final CurrencyDao currencyDao;
    private final UserDao userDao;
    private final IEOInfoRepository ieoInfoRepository;
    private final IEOResultRepository ieoResultRepository;
    private final WalletService walletService;
    private final CurrencyService currencyService;
    private final WalletDao walletDao;
    private final IEOQueueService ieoQueueService;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public IEOServiceImpl(IEOClaimRepository ieoClaimRepository,
                          CurrencyDao currencyDao,
                          UserDao userDao,
                          IEOInfoRepository ieoInfoRepository,
                          IEOResultRepository ieoResultRepository,
                          WalletService walletService, CurrencyService currencyService,
                          WalletDao walletDao,
                          IEOQueueService ieoQueueService) {
        this.ieoClaimRepository = ieoClaimRepository;
        this.currencyDao = currencyDao;
        this.userDao = userDao;
        this.ieoInfoRepository = ieoInfoRepository;
        this.ieoResultRepository = ieoResultRepository;
        this.walletService = walletService;
        this.currencyService = currencyService;
        this.walletDao = walletDao;
        this.ieoQueueService = ieoQueueService;
    }

    @Transactional
    @Override
    public ClaimDto addClaim(ClaimDto claimDto, String email) {

        if (!ieoClaimRepository.checkIfIeoOpenForCurrency(claimDto.getNameCurrency())) {
            String message = String.format("Failed to create claim whila IEO %s not started",
                    claimDto.getNameCurrency());
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_NOT_STARTED, message);
        }

        IEOInfo ieoInfo = ieoInfoRepository.findByCurrencyName(claimDto.getNameCurrency());
        User user = userDao.findByEmail(email);
        IEOClaim ieoClaim = new IEOClaim(claimDto.getNameCurrency(), ieoInfo.getUserId(), user.getId(), claimDto.getAmount(),
                ieoInfo.getRate());

        int currencyId = currencyService.findByName("BTC").getId();
        BigDecimal available = walletDao.getAvailableAmountInBtcLocked(ieoClaim.getUserId(), currencyId);
        if (available.compareTo(ieoClaim.getPriceInBtc()) < 0) {
            String message = String.format("Failed to apply as user has insufficient funds: suggested %s BTC, but available is %s BTC",
                    available.toPlainString(), ieoClaim.getPriceInBtc());
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_INSUFFICIENT_BUYER_FUNDS, message);
        }

        ieoClaim = ieoClaimRepository.create(ieoClaim);

        if (ieoClaim == null) {
            String message = "Failed to save user's claim";
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_CLAIM_SAVE_FAILURE, message);
        }
        boolean result = walletDao.reserveUserBtcForIeo(ieoClaim.getUserId(), ieoClaim.getPriceInBtc(), currencyId);
        if (!result) {
            String message = String.format("Failed to reserve %s BTC from user's account", ieoClaim.getPriceInBtc());
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_USER_RESERVE_BTC_FAILURE, message);
        }
        ieoQueueService.add(ieoClaim);
        claimDto.setId(ieoClaim.getId());
        return claimDto;
    }

    @Override
    public IEOStatusInfo checkUserStatusForIEO(String email) {
        User user = userDao.findByEmail(email);
        return null;
    }
}
