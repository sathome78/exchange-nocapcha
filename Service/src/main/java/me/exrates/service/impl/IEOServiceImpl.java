package me.exrates.service.impl;

import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IeoDetailsRepository;
import me.exrates.dao.UserDao;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEODetails;
import me.exrates.model.User;
import me.exrates.model.Wallet;
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

    private final CurrencyService currencyService;
    private final IEOClaimRepository ieoClaimRepository;
    private final IEOQueueService ieoQueueService;
    private final IeoDetailsRepository ieoDetailsRepository;
    private final UserDao userDao;
    private final WalletService walletService;

    @Autowired
    public IEOServiceImpl(IEOClaimRepository ieoClaimRepository,
                          UserDao userDao,
                          IeoDetailsRepository ieoDetailsRepository,
                          CurrencyService currencyService,
                          IEOQueueService ieoQueueService,
                          WalletService walletService) {
        this.ieoClaimRepository = ieoClaimRepository;
        this.userDao = userDao;
        this.ieoDetailsRepository = ieoDetailsRepository;
        this.currencyService = currencyService;
        this.walletService = walletService;
        this.ieoQueueService = ieoQueueService;
    }

    @Transactional
    @Override
    public ClaimDto addClaim(ClaimDto claimDto, String email) {

        IEODetails ieoDetails = ieoDetailsRepository.findOpenIeoByCurrencyName(claimDto.getCurrencyName());
        if (ieoDetails == null) {
            String message = String.format("Failed to create claim while IEO %s not started",
                    claimDto.getCurrencyName());
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_NOT_STARTED_YET, message);
        }

        User user = userDao.findByEmail(email);

        validateUserAmountRestrictions(ieoDetails, user, claimDto);

        IEOClaim ieoClaim = new IEOClaim(ieoDetails.getId(), claimDto.getCurrencyName(), ieoDetails.getMakerId(), user.getId(), claimDto.getAmount(),
                ieoDetails.getRate());

        int currencyId = currencyService.findByName("BTC").getId();
        BigDecimal available = walletService.getAvailableAmountInBtcLocked(user.getId(), currencyId);
        if (available.compareTo(ieoClaim.getPriceInBtc()) < 0) {
            String message = String.format("Failed to apply as user has insufficient funds: suggested %s BTC, but available is %s BTC",
                    available.toPlainString(), ieoClaim.getPriceInBtc());
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_INSUFFICIENT_BUYER_FUNDS, message);
        }

        ieoClaim = ieoClaimRepository.save(ieoClaim);

        if (ieoClaim == null) {
            String message = "Failed to save user's claim";
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_CLAIM_SAVE_FAILURE, message);
        }
        boolean result = walletService.reserveUserBtcForIeo(ieoClaim.getUserId(), ieoClaim.getPriceInBtc());
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

    private void validateUserAmountRestrictions(IEODetails ieoDetails, User user, ClaimDto claimDto) {
        if (ieoDetails.getMinAmount().compareTo(BigDecimal.ZERO) != 0
                && ieoDetails.getMinAmount().compareTo(claimDto.getAmount()) < 0) {
            String message = String.format("Failed to accept claim as minimal amount to buy is %s %s, but you submitted %s %s",
                    ieoDetails.getMinAmount().toPlainString(), ieoDetails.getCurrencyName(), claimDto.getAmount(), ieoDetails.getCurrencyName());
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_MIN_AMOUNT_FAILURE, message);
        } else if (ieoDetails.getMaxAmountPerClaim().compareTo(BigDecimal.ZERO) != 0
                && ieoDetails.getMaxAmountPerClaim().compareTo(claimDto.getAmount()) < 0) {
            String message = String.format("Failed to accept claim as maximum amount to buy is %s %s, but you submitted %s %s",
                    ieoDetails.getMaxAmountPerClaim().toPlainString(), ieoDetails.getCurrencyName(), claimDto.getAmount(), ieoDetails.getCurrencyName());
            LOGGER.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_MAX_AMOUNT_FAILURE, message);
        } else if (ieoDetails.getMaxAmountPerUser().compareTo(BigDecimal.ZERO) != 0) {
            Wallet userIeoWallet = walletService.findByUserAndCurrency(user.getId(), ieoDetails.getCurrencyName());
            if (userIeoWallet != null && userIeoWallet.getActiveBalance().compareTo(claimDto.getAmount()) > 0) {
                String message = String.format("Failed to accept claim as maximum amount per user to buy is %s %s, but you submitted only %s %s",
                        ieoDetails.getMaxAmountPerUser().toPlainString(), ieoDetails.getCurrencyName(), claimDto.getAmount(), ieoDetails.getCurrencyName());
                LOGGER.warn(message);
                throw new IeoException(ErrorApiTitles.IEO_MAX_AMOUNT_PER_USER_FAILURE, message);
            }
        }
    }
}
