package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IeoDetailsRepository;
import me.exrates.dao.IEOResultRepository;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEODetails;
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

@Service
public class IEOServiceImpl implements IEOService {
    private static final Logger LOGGER = LogManager.getLogger(IEOServiceImpl.class);

    private final IEOClaimRepository ieoClaimRepository;
    private final CurrencyDao currencyDao;
    private final UserDao userDao;
    private final IeoDetailsRepository ieoInfoRepository;
    private final IEOResultRepository ieoResultRepository;
    private final WalletService walletService;
    private final CurrencyService currencyService;
    private final WalletDao walletDao;
    private final IEOQueueService ieoQueueService;

    @Autowired
    public IEOServiceImpl(IEOClaimRepository ieoClaimRepository,
                          CurrencyDao currencyDao,
                          UserDao userDao,
                          IeoDetailsRepository ieoInfoRepository,
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

        IEODetails ieoDetails = ieoInfoRepository.findOpenIeoByCurrencyName(claimDto.getNameCurrency());
        if (ieoDetails == null) {
            String message = String.format("Failed to create claim while IEO %s not started",
                    claimDto.getNameCurrency());
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_NOT_STARTED_YET, message);
        }

        User user = userDao.findByEmail(email);
        IEOClaim ieoClaim = new IEOClaim(claimDto.getNameCurrency(), ieoDetails.getUserId(), user.getId(), claimDto.getAmount(),
                ieoDetails.getRate());

        int currencyId = currencyService.findByName("BTC").getId();
        BigDecimal available = walletDao.getAvailableAmountInBtcLocked(user.getId(), currencyId);
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
