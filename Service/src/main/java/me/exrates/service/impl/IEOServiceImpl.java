package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IEOInfoRepository;
import me.exrates.dao.IEOResultRepository;
import me.exrates.dao.KYCSettingsDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEOInfo;
import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.model.dto.ieo.IEOStatusInfo;
import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.enums.PolicyEnum;
import me.exrates.service.CurrencyService;
import me.exrates.service.IEOService;
import me.exrates.service.UserService;
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
    private final IEOInfoRepository ieoInfoRepository;
    private final IEOResultRepository ieoResultRepository;
    private final WalletService walletService;
    private final CurrencyService currencyService;
    private final WalletDao walletDao;
    private final IEOQueueService ieoQueueService;
    private final UserService userService;
    private final KYCSettingsDao kycSettingsDao;

    @Autowired
    public IEOServiceImpl(IEOClaimRepository ieoClaimRepository,
                          CurrencyDao currencyDao,
                          UserDao userDao,
                          IEOInfoRepository ieoInfoRepository,
                          IEOResultRepository ieoResultRepository,
                          WalletService walletService, CurrencyService currencyService,
                          WalletDao walletDao,
                          IEOQueueService ieoQueueService,
                          UserService userService,
                          KYCSettingsDao kycSettingsDao) {
        this.ieoClaimRepository = ieoClaimRepository;
        this.currencyDao = currencyDao;
        this.userDao = userDao;
        this.ieoInfoRepository = ieoInfoRepository;
        this.ieoResultRepository = ieoResultRepository;
        this.walletService = walletService;
        this.currencyService = currencyService;
        this.walletDao = walletDao;
        this.ieoQueueService = ieoQueueService;
        this.userService = userService;
        this.kycSettingsDao = kycSettingsDao;
    }

    @Transactional
    @Override
    public ClaimDto addClaim(ClaimDto claimDto, String email) {

        IEOInfo ieoInfo = ieoInfoRepository.findOpenIeoByCurrencyName(claimDto.getNameCurrency());
        if (ieoInfo == null) {
            String message = String.format("Failed to create claim while IEO %s not started",
                    claimDto.getNameCurrency());
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_NOT_STARTED_YET, message);
        }

        User user = userDao.findByEmail(email);
        IEOClaim ieoClaim = new IEOClaim(claimDto.getNameCurrency(), ieoInfo.getUserId(), user.getId(), claimDto.getAmount(),
                ieoInfo.getRate());

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

        String statusKyc = userService.getUserKycStatusByEmail(email);
        boolean kycCheck = statusKyc.equalsIgnoreCase("SUCCESS");
        KycCountryDto countryDto = null;
        if (kycCheck) {
             countryDto = kycSettingsDao.getCountryByCode(user.getCountry());
        }

        boolean policyCheck = userDao.existPolicyByUserIdAndPolicy(user.getId(), PolicyEnum.IEO.getName());

        //todo check by list county

        return new IEOStatusInfo(kycCheck, policyCheck, true, countryDto);
    }
}
