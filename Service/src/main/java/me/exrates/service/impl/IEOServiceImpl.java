package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IEOInfoRepository;
import me.exrates.dao.UserDao;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEOInfo;
import me.exrates.model.User;
import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.service.IEOService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IEOServiceImpl implements IEOService {
    private static final Logger LOGGER = LogManager.getLogger(IEOServiceImpl.class);

    private final IEOClaimRepository ieoClaimRepository;
    private final CurrencyDao currencyDao;
    private final UserDao userDao;
    private final IEOInfoRepository ieoInfoRepository;

    @Autowired
    public IEOServiceImpl(IEOClaimRepository ieoClaimRepository,
                          CurrencyDao currencyDao,
                          UserDao userDao,
                          IEOInfoRepository ieoInfoRepository) {
        this.ieoClaimRepository = ieoClaimRepository;
        this.currencyDao = currencyDao;
        this.userDao = userDao;
        this.ieoInfoRepository = ieoInfoRepository;
    }

    @Override
    public ClaimDto addClaim(ClaimDto claimDto, String email) {

        User user = userDao.findByEmail(email);
        IEOInfo ieoInfo = ieoInfoRepository.findByCurrencyName(claimDto.getNameCurrency());

        IEOClaim ieoClaim =
                new IEOClaim(claimDto.getNameCurrency(), ieoInfo.getUserId(), user.getId(), claimDto.getAmount());
        ieoClaimRepository.create(ieoClaim);
        claimDto.setId(ieoClaim.getId());
        return claimDto;
    }
}
