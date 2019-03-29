package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.util.BigDecimalConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceTestConfig.class})
public class CurrencyServiceImplTest {

    @Autowired
    private CurrencyService currencyService;

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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void getCurrencyName_Test() {
        when(currencyDao.getCurrencyName(anyInt())).thenReturn("name");
//        assertThat("name", is(currencyService.getCurrencyName(5)));
        assertEquals("name",currencyService.getCurrencyName(5));
        verify(currencyDao, times(1)).getCurrencyName(5);
    }

    @Test
    public void getAllActiveCurrencies_Test() {
        List<Currency> currencyList = Arrays.asList(new Currency());
        stub(currencyDao.getAllActiveCurrencies()).toReturn(currencyList);

        assertEquals(currencyList,currencyService.getAllActiveCurrencies());
        verify(currencyDao, times(1)).getAllActiveCurrencies();
    }

    @Test
    public void getAllCurrencies() {
        List<Currency> currencyList = Arrays.asList(new Currency());
        stub(currencyDao.getAllCurrencies()).toReturn(currencyList);

        assertEquals(currencyList,currencyService.getAllCurrencies());
        verify(currencyDao, times(1)).getAllCurrencies();
    }

    @Test
    public void findByName() {
        Currency currency = new Currency(8);
        when(currencyDao.findByName(anyString())).thenReturn(currency);

        assertEquals(currency,currencyService.findByName("test"));
        verify(currencyDao, times(1)).findByName("test");
    }

    @Test
    public void findById() {
        Currency currency = new Currency(8);
        when(currencyDao.findById(anyInt())).thenReturn(currency);

        assertEquals(currency,currencyService.findById(7));
        verify(currencyDao, times(1)).findById(7);
    }

    @Test
    public void findAllCurrencies() {
        List<Currency> currencyList = Arrays.asList(new Currency());
        when(currencyDao.findAllCurrencies()).thenReturn(currencyList);

        assertEquals(currencyList,currencyService.findAllCurrencies());
        verify(currencyDao, times(1)).findAllCurrencies();
    }

    @Test
    public void updateCurrencyLimit() {
        when(userRoleService.getRealUserRoleIdByBusinessRoleList(anyString())).thenReturn(Arrays.asList(5,6,7));
        doNothing().when(currencyDao).updateCurrencyLimit(anyInt(), any(OperationType.class), anyListOf(Integer.TYPE), any(BigDecimal.class), any(BigDecimal.class), any(Integer.class));
        currencyService.updateCurrencyLimit(6, OperationType.STORNO, "", new BigDecimal(5), new BigDecimal(5), 8);

        verify(userRoleService, times(1)).getRealUserRoleIdByBusinessRoleList("");
        verify(currencyDao, times(1)).updateCurrencyLimit(6, OperationType.STORNO, Arrays.asList(5,6,7), new BigDecimal(5), new BigDecimal(5), 8);
    }

    @Test
    public void updateCurrencyLimit1() {
        doNothing().when(currencyDao).updateCurrencyLimit(anyInt(), any(OperationType.class), any(BigDecimal.class), any(BigDecimal.class), any(Integer.class));
        currencyService.updateCurrencyLimit(6, OperationType.STORNO, new BigDecimal(5), new BigDecimal(5), 8);

        verify(currencyDao, times(1)).updateCurrencyLimit(6, OperationType.STORNO, new BigDecimal(5), new BigDecimal(5), 8);
    }

    @Test
    public void retrieveCurrencyLimitsForRole() {
        List<Integer> integerList = Arrays.asList(5,6,7,9);
        List<CurrencyLimit> currencyLimits = Arrays.asList(new CurrencyLimit());

        when(userRoleService.getRealUserRoleIdByBusinessRoleList(anyString())).thenReturn(integerList);
        when(currencyDao.retrieveCurrencyLimitsForRoles(anyList(), any(OperationType.class))).thenReturn(currencyLimits);

        assertEquals(currencyLimits,currencyService.retrieveCurrencyLimitsForRole("test", OperationType.INPUT));

        verify(userRoleService, times(1)).getRealUserRoleIdByBusinessRoleList("test");
        verify(currencyDao, times(1)).retrieveCurrencyLimitsForRoles(integerList,OperationType.INPUT);
    }

    @Test
    public void retrieveMinLimitForRoleAndCurrency() {
        when(currencyDao.retrieveMinLimitForRoleAndCurrency(any(UserRole.class), any(OperationType.class), anyInt())).thenReturn(new BigDecimal(10));

        assertEquals(new BigDecimal(10), currencyService.retrieveMinLimitForRoleAndCurrency(UserRole.USER, OperationType.BUY, 777));

        verify(currencyDao, times(1)).retrieveMinLimitForRoleAndCurrency(UserRole.USER, OperationType.BUY, 777);
    }

    @Test
    public void retrieveMaxDailyRequestForRoleAndCurrency() {
        when(currencyDao.retrieveMaxDailyRequestForRoleAndCurrency(any(UserRole.class), any(OperationType.class), anyInt())).thenReturn(new BigDecimal(10));

        assertEquals(new BigDecimal(10), currencyService.retrieveMaxDailyRequestForRoleAndCurrency(UserRole.USER, OperationType.BUY, 777));

        verify(currencyDao, times(1)).retrieveMaxDailyRequestForRoleAndCurrency(UserRole.USER, OperationType.BUY, 777);
    }

    @Test
    public void getAllCurrencyPairs() {
        when(currencyDao.getAllCurrencyPairs(any(CurrencyPairType.class))).thenReturn(Arrays.asList(new CurrencyPair()));

        assertEquals(Arrays.asList(new CurrencyPair()), currencyService.getAllCurrencyPairs(CurrencyPairType.MAIN));

        verify(currencyDao, times(1)).getAllCurrencyPairs(CurrencyPairType.MAIN);
    }

    @Test
    public void getAllCurrencyPairsWithHidden() {
        when(currencyDao.getAllCurrencyPairsWithHidden(any(CurrencyPairType.class))).thenReturn(Arrays.asList(new CurrencyPair()));

        assertEquals(Arrays.asList(new CurrencyPair()), currencyService.getAllCurrencyPairsWithHidden(CurrencyPairType.MAIN));

        verify(currencyDao, times(1)).getAllCurrencyPairsWithHidden(CurrencyPairType.MAIN);
    }

    @Test
    public void getAllCurrencyPairsInAlphabeticOrder() {
        List<CurrencyPair> result1 = Arrays.asList(
                new CurrencyPair("aaa"),
                new CurrencyPair("zzz"),
                new CurrencyPair("bbb"),
                new CurrencyPair("hhh"));

        List<CurrencyPair> result = Arrays.asList(
                new CurrencyPair("hhh"),
                new CurrencyPair("aaa"),
                new CurrencyPair("zzz"),
                new CurrencyPair("bbb"));
//        result.sort(Comparator.comparing(CurrencyPair::getName));
//        SORT METHOD???
//        assertThat();
        when(currencyDao.getAllCurrencyPairs(any(CurrencyPairType.class))).thenReturn(result);
        assertEquals(result1.get(0),currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.MAIN).get(0));

        verify(currencyDao, times(1)).getAllCurrencyPairs(CurrencyPairType.MAIN);
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