package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.TransactionDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.dao.configuration.TestConfiguration;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.enums.MerchantProcessType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class WalletDaoImplTest {

    private static final String EMAIL = "shvets.k@gmail.com";
    private static final List<Integer> WITHDRAW_STATUS_IDS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
    private static final List<MerchantProcessType> PROCESS_TYPE = Collections.singletonList(MerchantProcessType.CRYPTO);

    @Mock
    private TransactionDao transactionDao;
    @Mock
    private UserDao userDao;
    @Mock
    private CurrencyDao currencyDao;
    @Mock
    private NamedParameterJdbcTemplate masteJdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    private WalletDao walletDao;

    @Before
    public void setUp() {
        walletDao = new WalletDaoImpl(transactionDao, userDao, currencyDao, masteJdbcTemplate, slaveJdbcTemplate);
    }

    @Test
    public void getAllWalletsForUserDetailedTest() {
        List<MyWalletsDetailedDto> allWalletsForUserDetailed = walletDao.getAllWalletsForUserDetailed(EMAIL, WITHDRAW_STATUS_IDS, Locale.ENGLISH, PROCESS_TYPE);

        assertNotNull(allWalletsForUserDetailed);
        assertFalse(allWalletsForUserDetailed.isEmpty());
    }
}