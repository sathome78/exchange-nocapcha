package me.exrates.service.impl;

import me.exrates.dao.InputOutputDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.CommissionDataDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.*;
import me.exrates.model.vo.CacheData;
import me.exrates.service.*;
import me.exrates.service.exception.UnsupportedMerchantException;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.valueOf;
import static java.util.Collections.EMPTY_LIST;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.RefillStatusEnum.ON_BCH_EXAM;

/**
 * created by ValkSam
 */

@Service
public class InputOutputServiceImpl implements InputOutputService {

  private static final Logger log = LogManager.getLogger("inputoutput");

  @Autowired
  private MessageSource messageSource;

  @Autowired
  InputOutputDao inputOutputDao;

  @Autowired
  private CommissionService commissionService;

  @Autowired
  private UserService userService;

  @Autowired
  private WalletService walletService;

  @Autowired
  private MerchantService merchantService;

  @Autowired
  private CurrencyService currencyService;

  @Override
  @Transactional(readOnly = true)
  public List<MyInputOutputHistoryDto> getMyInputOutputHistory(
      CacheData cacheData,
      String email,
      Integer offset, Integer limit,
      Locale locale) {
    List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
        .stream()
        .map(OperationType::getType)
        .collect(Collectors.toList());
    List<MyInputOutputHistoryDto> result = inputOutputDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<MyInputOutputHistoryDto>() {{
        add(new MyInputOutputHistoryDto(false));
      }};
    } else {
      result.forEach(e ->
      {
        e.setSummaryStatus(generateAndGetSummaryStatus(e, locale));
        e.setButtons(generateAndGetButtonsSet(e.getStatus(), null, false, locale));
        e.setAuthorisedUserId(e.getUserId());
      });
    }
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<MyInputOutputHistoryDto> getMyInputOutputHistory(
      String email,
      Integer offset, Integer limit,
      Locale locale) {
    List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
        .stream()
        .map(OperationType::getType)
        .collect(Collectors.toList());
    List<MyInputOutputHistoryDto> result = inputOutputDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
    result.forEach(e ->
    {
      e.setSummaryStatus(generateAndGetSummaryStatus(e, locale));
      e.setButtons(generateAndGetButtonsSet(e.getStatus(), null, false, locale));
      e.setAuthorisedUserId(e.getUserId());
    });
    return result;
  }

  @Override
  public List<Map<String, Object>> generateAndGetButtonsSet(
      InvoiceStatus status,
      InvoiceOperationPermission permittedOperation,
      boolean authorisedUserIsHolder,
      Locale locale) {
    if (status == null) return EMPTY_LIST;
    InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue = InvoiceActionTypeEnum.InvoiceActionParamsValue.builder()
        .authorisedUserIsHolder(authorisedUserIsHolder)
        .permittedOperation(permittedOperation)
        .build();
    return status.getAvailableActionList(paramsValue).stream()
        .filter(e -> e.getActionTypeButton() != null)
        .map(e -> new HashMap<String, Object>(e.getActionTypeButton().getProperty()))
        .peek(e -> e.put("buttonTitle", messageSource.getMessage((String) e.get("buttonTitle"), null, locale)))
        .collect(Collectors.toList());
  }


  private String generateAndGetSummaryStatus(MyInputOutputHistoryDto row, Locale locale) {
    switch (row.getSourceType()) {
      case REFILL: {
        RefillStatusEnum status = (RefillStatusEnum) row.getStatus();
        if (status == ON_BCH_EXAM) {
          String confirmations = row.getConfirmation() == null ? "0" : row.getConfirmation().toString();
          String message = confirmations.concat("/").concat(String.valueOf(BitcoinService.CONFIRMATION_NEEDED_COUNT));
          return message;
        } else {
          return messageSource.getMessage("merchants.refill.".concat(status.name()), null, locale);
        }
      }
      case WITHDRAW: {
        WithdrawStatusEnum status = (WithdrawStatusEnum) row.getStatus();
        return messageSource.getMessage("merchants.withdraw.".concat(status.name()), null, locale);
      }
      default: {
        return row.getTransactionProvided();
      }
    }
  }

  @Override
  @Transactional
  public Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail) {
    merchantService.checkMerchantIsBlocked(payment.getMerchant(), payment.getCurrency(), payment.getOperationType());
    OperationType operationType = payment.getOperationType();
    BigDecimal amount = valueOf(payment.getSum());
    Merchant merchant = merchantService.findById(payment.getMerchant());
    Currency currency = currencyService.findById(payment.getCurrency());
    String destination = payment.getDestination();
    if (!"CRYPTO".equals(merchant.getProcessType()) || amount.compareTo(BigDecimal.ZERO) != 0) {
      try {
        merchantService.checkAmountForMinSum(merchant.getId(), currency.getId(), amount);
      } catch (EmptyResultDataAccessException e) {
        final String exceptionMessage = "MerchantService".concat(operationType == INPUT ?
            "Input" : "Output");
        throw new UnsupportedMerchantException(exceptionMessage);
      }
    }
    User user = userService.findByEmail(userEmail);
    Wallet wallet = walletService.findByUserAndCurrency(user, currency);
    CommissionDataDto commissionData = commissionService.normalizeAmountAndCalculateCommission(
        user.getId(),
        amount,
        operationType,
        currency.getId(),
        merchant.getId());
    TransactionSourceType transactionSourceType = operationType.getTransactionSourceType();
    CreditsOperation creditsOperation = new CreditsOperation.Builder()
        .initialAmount(commissionData.getAmount())
        .amount(commissionData.getResultAmount())
        .commissionAmount(commissionData.getCompanyCommissionAmount())
        .commission(commissionData.getCompanyCommission())
        .operationType(operationType)
        .user(user)
        .currency(currency)
        .wallet(wallet)
        .merchant(merchant)
        .destination(destination)
        .transactionSourceType(transactionSourceType)
        .build();
    return Optional.of(creditsOperation);
  }

}
