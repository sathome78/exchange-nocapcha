package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.util.BigDecimalConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class CurrencyServiceImplTest {

    @InjectMocks
    private CurrencyServiceImpl currencyServiceImpl;

    @Mock
    private CurrencyDao currencyDao;
    @Mock
    private UserService userService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private ExchangeApi exchangeApi;

    @Mock
    private BigDecimalConverter converter;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(currencyServiceImpl)
                .build();
    }

    @Test
    public void getCurrencyName_Test() {
        when(currencyDao.getCurrencyName(5)).thenReturn("name");

        assertEquals("name",currencyServiceImpl.getCurrencyName(5));
    }

    @Test
    public void getAllActiveCurrencies_Test() {
        List<Currency> currencyList = new ArrayList<>();
        when(currencyDao.getAllActiveCurrencies()).thenReturn(currencyList);

        assertEquals(currencyList,currencyServiceImpl.getAllActiveCurrencies());
    }

    @Test
    public void getAllCurrencies() {
        List<Currency> currencyList = new ArrayList<>();
        when(currencyDao.getAllCurrencies()).thenReturn(currencyList);

        assertEquals(currencyList,currencyServiceImpl.getAllCurrencies());
    }

    @Test
    public void findByName() {
        Currency currency = new Currency(8);
        when(currencyDao.findByName(anyString())).thenReturn(currency);

        assertEquals(currency,currencyServiceImpl.findByName("test"));
    }

    @Test
    public void findById() {
        Currency currency = new Currency(8);
        when(currencyDao. findById(anyInt())).thenReturn(currency);

        assertEquals(currency,currencyServiceImpl.findById(1));

    }

    @Test
    public void findAllCurrencies() {
        List<Currency> currencyList = new ArrayList<>();
        when(currencyDao.findAllCurrencies()).thenReturn(currencyList);

        assertEquals(currencyList,currencyServiceImpl.findAllCurrencies());

    }

    @Test
    public void updateCurrencyLimit() {
        List<Integer> integerList = new ArrayList<>();
        integerList.add(5);
        when(userRoleService.getRealUserRoleIdByBusinessRoleList(anyString())).thenReturn(integerList);
        doNothing().when(currencyDao).updateCurrencyLimit(anyInt(), any(OperationType.class), anyListOf(Integer.TYPE), any(BigDecimal.class), any(BigDecimal.class), any(Integer.class));
        assertEquals(integerList,userRoleService.getRealUserRoleIdByBusinessRoleList(anyString()));
    }

    @Test
    public void updateCurrencyLimit1() {
        doNothing().when(currencyDao).updateCurrencyLimit(anyInt(), any(OperationType.class), any(BigDecimal.class), any(BigDecimal.class), any(Integer.class));
    }

    @Test
    public void retrieveCurrencyLimitsForRole() {
        List<Integer> integerList = new ArrayList<>();
        when(userRoleService.getRealUserRoleIdByBusinessRoleList(anyString())).thenReturn(integerList);

        List<CurrencyLimit> currencyLimits = new ArrayList<>();
        when(currencyDao.retrieveCurrencyLimitsForRoles(anyList(), any(OperationType.class))).thenReturn(currencyLimits);

        assertEquals(integerList,userRoleService.getRealUserRoleIdByBusinessRoleList(anyString()));
        assertEquals(currencyLimits,currencyDao.retrieveCurrencyLimitsForRoles(anyList(), any(OperationType.class)));
    }

    @Test
    public void retrieveMinLimitForRoleAndCurrency() {
    }

    @Test
    public void retrieveMaxDailyRequestForRoleAndCurrency() {
    }

    @Test
    public void getAllCurrencyPairs() {
    }

    @Test
    public void getAllCurrencyPairsWithHidden() {
    }

    @Test
    public void getAllCurrencyPairsInAlphabeticOrder() {
    }

    @Test
    public void findCurrencyPairById() {
    }

    @Test
    public void amountToString() {
    }

    @Test
    public void resolvePrecision() {
    }

    @Test
    public void resolvePrecisionByOperationType() {
    }

    @Test
    public void retrieveMinTransferLimits() {
    }

    @Test
    public void findWithOperationPermissionByUserAndDirection() {
    }

    @Test
    public void getCurrencyOperationPermittedForRefill() {
    }

    @Test
    public void getAllCurrencyOperationPermittedForRefill() {
    }

    @Test
    public void getCurrencyOperationPermittedForWithdraw() {
    }

    @Test
    public void getAllCurrencyOperationPermittedForWithdraw() {
    }

    @Test
    public void getCurrencyPermittedNameList() {
    }

    @Test
    public void getCurrencyPermittedOperationList() {
    }

    @Test
    public void getCurrencyPermittedNameList1() {
    }

    @Test
    public void getWarningForCurrency() {
    }

    @Test
    public void getWarningsByTopic() {
    }

    @Test
    public void getWarningForMerchant() {
    }

    @Test
    public void getById() {
    }

    @Test
    public void findLimitForRoleByCurrencyPairAndType() {
    }

    @Test
    public void findLimitForRoleByCurrencyPairAndTypeAndUser() {
    }

    @Test
    public void findAllCurrencyLimitsForRoleAndType() {
    }

    @Test
    public void updateCurrencyPairLimit() {
    }

    @Test
    public void findCurrencyPairsWithLimitsForUser() {
    }

    @Test
    public void findAllCurrenciesWithHidden() {
    }

    @Test
    public void computeRandomizedAddition() {
    }

    @Test
    public void isIco() {
    }

    @Test
    public void getCurrencyScaleByCurrencyId() {
    }

    @Test
    public void getCurrencyPairByName() {
    }

    @Test
    public void findCurrencyPairIdByName() {
    }

    @Test
    public void findAllCurrenciesByProcessType() {
    }

    @Test
    public void findPermitedCurrencyPairs() {
    }

    @Test
    public void getNotHiddenCurrencyPairByName() {
    }

    @Test
    public void findActiveCurrencyPairs() {
    }

    @Test
    public void findAllCurrency() {
    }

    @Test
    public void updateVisibilityCurrencyById() {
    }

    @Test
    public void findAllCurrencyPair() {
    }

    @Test
    public void updateVisibilityCurrencyPairById() {
    }

    @Test
    public void updateAccessToDirectLinkCurrencyPairById() {
    }

    @Test
    public void getStatsByCoin() {
    }

    @Test
    public void setPropertyCalculateLimitToUsd() {
    }

    @Test
    public void updateWithdrawLimits() {
    }

    @Test
    public void getCurrencies() {
    }

    @Test
    public void getPairsByFirstPartName() {
    }

    @Test
    public void getPairsBySecondPartName() {
    }
}