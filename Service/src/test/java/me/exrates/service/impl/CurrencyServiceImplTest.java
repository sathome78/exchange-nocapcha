package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.util.BigDecimalConverter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
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

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ExchangeApi exchangeApi;

    @Autowired
    private BigDecimalConverter converter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("USER", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("USER")));
    }

    @Test
    public void getCurrencyName_Test() {
        when(currencyDao.getCurrencyName(anyInt())).thenReturn("name");

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
        CurrencyPair currencyPair = new CurrencyPair("New Curr");
        when(currencyDao.getAllCurrencyPairs(anyObject())).thenReturn(Arrays.asList(currencyPair));

        assertEquals(Arrays.asList(currencyPair), currencyService.getAllCurrencyPairs(CurrencyPairType.ICO));

        verify(currencyDao, times(1)).getAllCurrencyPairs(CurrencyPairType.ICO);
    }

    @Ignore
    public void getAllCurrencyPairsWithHidden() {
        when(currencyDao.getAllCurrencyPairsWithHidden(any(CurrencyPairType.class))).thenReturn(Arrays.asList(new CurrencyPair("Some Curr")));

        assertEquals(Arrays.asList(new CurrencyPair("Some Curr")), currencyService.getAllCurrencyPairsWithHidden(CurrencyPairType.MAIN));

        verify(currencyDao, times(1)).getAllCurrencyPairsWithHidden(CurrencyPairType.MAIN);
    }

    @Test
    public void getAllCurrencyPairsInAlphabeticOrder() {
        List<CurrencyPair> result = Arrays.asList(
                new CurrencyPair("kkk"),
                new CurrencyPair("fff"),
                new CurrencyPair("aaa"),
                new CurrencyPair("zzz"));

        List<CurrencyPair> result2 = Arrays.asList(
                new CurrencyPair("aaa"),
                new CurrencyPair("fff"),
                new CurrencyPair("kkk"),
                new CurrencyPair("zzz"));
        when(currencyDao.getAllCurrencyPairs(any(CurrencyPairType.class))).thenReturn(result);

        assertEquals(result2,currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.MAIN));

        verify(currencyDao, times(1)).getAllCurrencyPairs(CurrencyPairType.MAIN);
    }

    @Ignore
    public void findCurrencyPairById_WhenOk() {
        when(currencyDao.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("Curr"));
        assertEquals(new CurrencyPair("Curr"), currencyService.findCurrencyPairById(7));

// TODO was 2 times
        verify(currencyDao, times(1)).findCurrencyPairById(7);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(expected = CurrencyPairNotFoundException.class)
    public void findCurrencyPairById_WhenException() {
        when(currencyDao.findCurrencyPairById(anyInt())).thenThrow(EmptyResultDataAccessException.class);
        currencyService.findCurrencyPairById(7);
        thrown.expect(CurrencyPairNotFoundException.class);
        thrown.expectMessage("Currency pair not found");
// TODO check method for times()
        verify(currencyDao, times(1)).findCurrencyPairById(7);
    }

    @Test
    public void amountToString() {
        assertEquals("5.00000000",currencyService.amountToString(new BigDecimal(5), "EDR"));
    }

    @Test
    public void resolvePrecision() {
        assertEquals(8, currencyService.resolvePrecision("EDR"));
        assertEquals(2, currencyService.resolvePrecision("EDR2"));
    }

    @Test
    public void resolvePrecisionByOperationType_WhenEDC_OUTPUT_PRECISION() {
        Currency currency = new Currency(7);
        currency.setName("EDR");
        when(currencyDao.findByName(anyString())).thenReturn(currency);

        assertEquals(3, currencyService.resolvePrecisionByOperationType("EDR", OperationType.OUTPUT));
        verify(currencyDao, times(1)).findByName(anyString());
    }

    @Test
    public void resolvePrecisionByOperationType_WhenCRYPTO_PRECISION() {
        Currency currency = new Currency(7);
        currency.setName("EDR");
        when(currencyDao.findByName(anyString())).thenReturn(currency);

        assertEquals(8, currencyService.resolvePrecisionByOperationType("EDR", OperationType.STORNO));
        verify(currencyDao, times(1)).findByName(anyString());
    }

    @Test
    public void resolvePrecisionByOperationType_WhenDEFAULT_PRECISION() {
        Currency currency = new Currency(7);
        currency.setName("EDR2");
        when(currencyDao.findByName(anyString())).thenReturn(currency);

        assertEquals(2, currencyService.resolvePrecisionByOperationType("rfrEDR", OperationType.STORNO));
        verify(currencyDao, times(1)).findByName(anyString());
    }

    @Ignore
    public void retrieveMinTransferLimits() {
        List<TransferLimitDto> transferLimitDtos = Arrays.asList(new TransferLimitDto());
//        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(currencyDao.retrieveMinTransferLimits(anyList(), anyInt())).thenReturn(transferLimitDtos);
// TODO me.exrates.service.exception.AuthenticationNotAvailableException
        assertEquals(transferLimitDtos, currencyService.retrieveMinTransferLimits(Arrays.asList(5,6,7)));
    }

    @Test
    public void findWithOperationPermissionByUserAndDirection() {
        List<UserCurrencyOperationPermissionDto> list = new ArrayList<>();
        UserCurrencyOperationPermissionDto entity = new UserCurrencyOperationPermissionDto();
        entity.setCurrencyName("FFF");
        list.add(entity);
        when(currencyDao.findCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(list);

        assertEquals(list, currencyService.findWithOperationPermissionByUserAndDirection(90, InvoiceOperationDirection.REFILL));
        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserAndDirection(90, "REFILL");
    }

    @Test
    public void getCurrencyOperationPermittedForRefill() {
        List<UserCurrencyOperationPermissionDto> list = new ArrayList<>();
        UserCurrencyOperationPermissionDto entity = new UserCurrencyOperationPermissionDto();
        entity.setCurrencyName("FFF");
        list.add(entity);

        when(userService.getIdByEmail(anyString())).thenReturn(99);
        when(currencyDao.findCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(list);

        assertEquals(list, currencyService.getCurrencyOperationPermittedForRefill("email@email.com"));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserAndDirection(99,"REFILL");

    }

    @Test
    public void getAllCurrencyOperationPermittedForRefill() {
        List<UserCurrencyOperationPermissionDto> list = new ArrayList<>();
        UserCurrencyOperationPermissionDto entity = new UserCurrencyOperationPermissionDto();
        entity.setCurrencyName("FFF");
        list.add(entity);

        when(userService.getIdByEmail(anyString())).thenReturn(99);
        when(currencyDao.findAllCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(list);

        assertEquals(list, currencyService.getAllCurrencyOperationPermittedForRefill("email@email.com"));

        verify(currencyDao, times(1)).findAllCurrencyOperationPermittedByUserAndDirection(99, "REFILL");
    }

    @Test
    public void getCurrencyOperationPermittedForWithdraw() {
        List<UserCurrencyOperationPermissionDto> list = new ArrayList<>();
        UserCurrencyOperationPermissionDto entity = new UserCurrencyOperationPermissionDto();
        entity.setCurrencyName("FFF");
        list.add(entity);

        when(userService.getIdByEmail(anyString())).thenReturn(99);
        when(currencyDao.findCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(list);
        assertEquals(list, currencyService.getCurrencyOperationPermittedForWithdraw("email@email.com"));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserAndDirection(99,"WITHDRAW");
    }

    @Test
    public void getAllCurrencyOperationPermittedForWithdraw() {
        List<UserCurrencyOperationPermissionDto> list = new ArrayList<>();
        UserCurrencyOperationPermissionDto entity = new UserCurrencyOperationPermissionDto();
        entity.setCurrencyName("FFF");
        list.add(entity);

        when(userService.getIdByEmail(anyString())).thenReturn(99);
        when(currencyDao.findAllCurrencyOperationPermittedByUserAndDirection(anyInt(), anyString())).thenReturn(list);

        assertEquals(list, currencyService.getAllCurrencyOperationPermittedForWithdraw("email@email.com"));

        verify(currencyDao, times(1)).findAllCurrencyOperationPermittedByUserAndDirection(99,"WITHDRAW");
    }

    @Test
    public void getCurrencyPermittedNameList_WhenParameterString() {
        List<UserCurrencyOperationPermissionDto> list = new ArrayList<>();
        UserCurrencyOperationPermissionDto entity = new UserCurrencyOperationPermissionDto();
        entity.setCurrencyName("FFF");
        list.add(entity);
        Set<String> set = new HashSet<>();
        set.add("FFF");

        when(userService.getIdByEmail(anyString())).thenReturn(90);
        when(currencyDao.findCurrencyOperationPermittedByUserList(anyInt())).thenReturn(list);

        assertEquals(set, currencyService.getCurrencyPermittedNameList("email@email.com"));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserList(90);

    }

    @Test
    public void getCurrencyPermittedOperationList() {
        List<UserCurrencyOperationPermissionDto> list = new ArrayList<>();
        UserCurrencyOperationPermissionDto entity = new UserCurrencyOperationPermissionDto();
        entity.setCurrencyName("FFF");
        list.add(entity);

        when(currencyDao.findCurrencyOperationPermittedByUserList(anyInt())).thenReturn(list);

        assertEquals(list, currencyService.getCurrencyPermittedOperationList(37));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserList(37);

    }

    @Test
    public void getCurrencyPermittedNameList_WhenParameterInteger() {
        List<UserCurrencyOperationPermissionDto> list = new ArrayList<>();
        UserCurrencyOperationPermissionDto entity = new UserCurrencyOperationPermissionDto();
        entity.setCurrencyName("FFF");
        list.add(entity);
        Set<String> set = new HashSet<>();
        set.add("FFF");

        when(currencyDao.findCurrencyOperationPermittedByUserList(anyInt())).thenReturn(list);

        assertEquals(set, currencyService.getCurrencyPermittedNameList(25));

        verify(currencyDao, times(1)).findCurrencyOperationPermittedByUserList(25);
    }

    @Test
    public void getWarningForCurrency() {
        List<String> list = Arrays.asList("list", "string");
        when(currencyDao.getWarningForCurrency(anyInt(), any(UserCommentTopicEnum.class))).thenReturn(list);

        assertEquals(list, currencyService.getWarningForCurrency(77, UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING));

        verify(currencyDao, times(1)).getWarningForCurrency(77, UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING);
    }

    @Test
    public void getWarningsByTopic() {
        when(currencyDao.getWarningsByTopic(any(UserCommentTopicEnum.class))).thenReturn(Arrays.asList("list", "string"));

        assertEquals(Arrays.asList("list", "string"), currencyService.getWarningsByTopic(UserCommentTopicEnum.GENERAL));

        verify(currencyDao, times(1)).getWarningsByTopic(UserCommentTopicEnum.GENERAL);
    }

    @Test
    public void getWarningForMerchant() {
        when(currencyDao.getWarningForMerchant(anyInt(),any(UserCommentTopicEnum.class))).thenReturn(Arrays.asList("list", "string"));

        assertEquals(Arrays.asList("list", "string"), currencyService.getWarningForMerchant(80, UserCommentTopicEnum.GENERAL));

        verify(currencyDao, times(1)).getWarningForMerchant(80, UserCommentTopicEnum.GENERAL);
    }

    @Test
    public void getById() {
        when(currencyDao.findById(anyInt())).thenReturn(new Currency(10));

        assertEquals(new Currency(10), currencyService.getById(40));

        verify(currencyDao, times(1)).findById(40);
    }

    @Test
    public void findLimitForRoleByCurrencyPairAndType() {
        CurrencyPairLimitDto currencyPairLimitDto = new CurrencyPairLimitDto();
        currencyPairLimitDto.setCurrencyPairName("String");

        when(currencyDao.findCurrencyPairLimitForRoleByPairAndType(anyInt(), anyInt(), anyInt())).thenReturn(currencyPairLimitDto);

        assertEquals(currencyPairLimitDto, currencyService.findLimitForRoleByCurrencyPairAndType(49, OperationType.SELL));

        verify(currencyDao, times(1)).findCurrencyPairLimitForRoleByPairAndType(49,4,1);
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