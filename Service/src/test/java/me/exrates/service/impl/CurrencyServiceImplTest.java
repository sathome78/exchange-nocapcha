package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class CurrencyServiceImplTest {

    @InjectMocks
    private CurrencyServiceImpl currencyServiceImpl;

    @Mock
    private CurrencyDao currencyDao;

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
    }

    @Test
    public void findByName() {
    }

    @Test
    public void findById() {
    }

    @Test
    public void findAllCurrencies() {
    }

    @Test
    public void updateCurrencyLimit() {
    }

    @Test
    public void updateCurrencyLimit1() {
    }

    @Test
    public void retrieveCurrencyLimitsForRole() {
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