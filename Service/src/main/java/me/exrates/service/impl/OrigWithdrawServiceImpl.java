package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PagingData;
import me.exrates.model.Transaction;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.dto.MerchantCurrencyAutoParamDto;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.*;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;
import static me.exrates.model.enums.WithdrawalRequestStatus.*;
import static me.exrates.model.enums.invoice.PendingPaymentStatusEnum.ON_BCH_EXAM;

public class OrigWithdrawServiceImpl extends BaseWithdrawServiceImpl {

  @Autowired
  private UserService userService;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private WithdrawRequestDao withdrawRequestDao;

  @Autowired
  private WalletService walletService;

  @Autowired
  private MerchantDao merchantDao;

  private static final Logger LOG = LogManager.getLogger("merchant");

  @Override
  @Transactional
  public List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(
      String startDate,
      String endDate,
      List<Integer> roleIdList,
      List<Integer> currencyList) {
    return withdrawRequestDao.findAllByDateIntervalAndRoleAndCurrency(startDate, endDate, roleIdList, currencyList);
  }

  @Override
  @Transactional
  public Map<String, String> acceptWithdrawalRequest(final int requestId,
                                                     final Locale locale,
                                                     final Principal principal) {
    final Optional<WithdrawRequest> withdraw = withdrawRequestDao.findById(requestId);
    if (!withdraw.isPresent()) {
      return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError", null, locale));
    }
    final WithdrawRequest request = withdraw.get();
    request.setProcessedBy(principal.getName());
    request.setStatus(ACCEPTED);
    withdrawRequestDao.update(request);
    final Optional<WithdrawRequest> withdrawUpdated = withdrawRequestDao.findById(requestId);
    if (!withdrawUpdated.isPresent()) {
      return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError", null, locale));
    }
    final WithdrawRequest requestUpdated = withdrawUpdated.get();
    transactionService.provideTransaction(request.getTransaction());
    Locale userLocale = new Locale(userService.getPreferedLang(request.getUserId()));
    sendWithdrawalNotification(request, ACCEPTED, userLocale);
    final HashMap<String, String> params = new HashMap<>();
    final String message = messageSource.getMessage("merchants.WithdrawRequestAccept", null, locale);
    params.put("success", message);
    params.put("acceptance", requestUpdated.getAcceptance().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    params.put("email", principal.getName());
    return params;
  }

  @Override
  @Transactional
  public Map<String, Object> declineWithdrawalRequest(final int requestId, final Locale locale, String email) {
    final Optional<WithdrawRequest> withdraw = withdrawRequestDao.findById(requestId);
    if (!withdraw.isPresent()) {
      return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError", null, locale));
    }
    final WithdrawRequest request = withdraw.get();
    request.setProcessedBy(email);
    request.setStatus(DECLINED);
    final Transaction transaction = request.getTransaction();
    withdrawRequestDao.update(request);
    final Optional<WithdrawRequest> withdrawUpdated = withdrawRequestDao.findById(requestId);
    if (!withdrawUpdated.isPresent()) {
      return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError", null, locale));
    }
    final WithdrawRequest requestUpdated = withdrawUpdated.get();
    final BigDecimal amount = transaction.getAmount().add(transaction.getCommissionAmount());
    walletService.withdrawReservedBalance(transaction.getUserWallet(), amount);
    walletService.depositActiveBalance(transaction.getUserWallet(), amount);
    Locale userLocale = new Locale(userService.getPreferedLang(request.getUserId()));
    sendWithdrawalNotification(request, DECLINED, userLocale);
    final HashMap<String, Object> params = new HashMap<>();
    final String message = messageSource.getMessage("merchants.WithdrawRequestDecline", null, locale);
    params.put("success", message);
    params.put("acceptance", requestUpdated.getAcceptance().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    params.put("email", email);
    params.put("userEmail", request.getUserEmail());
    return params;
  }

  @Override
  public List<WithdrawRequest> findAllWithdrawRequests() {
    return withdrawRequestDao.findAll();
  }

  @Override
  public DataTable<List<WithdrawRequest>> findWithdrawRequestsByStatus(Integer requestStatus, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData, String userEmail) {
    PagingData<List<WithdrawRequest>> result = withdrawRequestDao.findByStatus(requestStatus, userService.getIdByEmail(userEmail), dataTableParams, withdrawFilterData);
    LOG.debug(result.getData().stream().map(request -> request.getTransaction().getId()).collect(Collectors.toList()));
    DataTable<List<WithdrawRequest>> output = new DataTable<>();
    output.setData(result.getData());
    output.setRecordsTotal(result.getTotal());
    output.setRecordsFiltered(result.getFiltered());
    return output;
  }

  @Override
  @Transactional
  public Map<String, String> withdrawRequest(final CreditsOperation creditsOperation,
                                             WithdrawData withdrawData, final String userEmail, final Locale locale) {
    final Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
    final BigDecimal reserved = transaction
        .getAmount()
        .add(transaction.getCommissionAmount()).setScale(currencyService.resolvePrecision(creditsOperation.getCurrency().getName()), BigDecimal.ROUND_HALF_UP);
    walletService.depositReservedBalance(transaction.getUserWallet(), reserved);
    final WithdrawRequest request = new WithdrawRequest();
    request.setUserEmail(userEmail);
    if (creditsOperation.getDestination().isPresent() && !creditsOperation.getDestination().get().isEmpty()) {
      request.setWallet(creditsOperation.getDestination().get());
    } else {
      request.setWallet(withdrawData.getUserAccount());
    }
    creditsOperation
        .getMerchantImage()
        .ifPresent(request::setMerchantImage);
    request.setTransaction(transaction);
    request.setRecipientBankName(withdrawData.getRecipientBankName());
    request.setRecipientBankCode(withdrawData.getRecipientBankCode());
    request.setUserFullName(withdrawData.getUserFullName());
    request.setRemark(withdrawData.getRemark());
    withdrawRequestDao.create(request);
    transactionService.setSourceId(transaction.getId(), transaction.getId());

    String notification = null;
    try {
      notification = sendWithdrawalNotification(request, NEW, locale);
    } catch (final MailException e) {
      LOG.error(e);
    }
    final BigDecimal newAmount = transaction
        .getUserWallet()
        .getActiveBalance();
    final String currency = transaction
        .getCurrency()
        .getName();
    final String balance = currency + " " + currencyService.amountToString(newAmount, currency);
    final Map<String, String> result = new HashMap<>();
    result.put("success", notification);
    result.put("balance", balance);
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public MerchantCurrencyAutoParamDto getAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId) {
    LOG.error("NOT IMPLEMENTED");
    throw new NotImplimentedMethod("method NOT IMPLEMENTED !");
  }

  @Override
  public List<MyInputOutputHistoryDto> getMyInputOutputHistory(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale) {
    List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
        .stream()
        .map(OperationType::getType)
        .collect(Collectors.toList());
    List<MyInputOutputHistoryDto> result = withdrawRequestDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<MyInputOutputHistoryDto>() {{
        add(new MyInputOutputHistoryDto(false));
      }};
    } else {
      result.forEach(e ->
      {
        e.setSummaryStatus(generateAndGetSummaryStatus(e, locale));
        e.setButtons(generateAndGetButtonsSet(e, locale));
      });
    }
    return result;
  }

}
