package me.exrates.dao;

import me.exrates.model.Merchant;
import me.exrates.model.PagingData;
import me.exrates.model.Transaction;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.TransactionFlatForReportDto;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionDao {

  Transaction create(Transaction transaction);

  boolean updateForProvided(Transaction transaction);

  Transaction findById(int id);

  PagingData<List<Transaction>> findAllByUserWallets(List<Integer> walletIds, Integer status,
                                                     List<TransactionType> types, List<Integer> merchantIds,
                                                     String dateFrom, String dateTo,
                                                     BigDecimal fromAmount, BigDecimal toAmount,
                                                     BigDecimal fromCommissionAmount, BigDecimal toCommissionAmount,
                                                     int offset, int limit,
                                                     String sortColumn, String sortDirection, Locale locale);

  boolean provide(int id);

  boolean delete(int id);

  void updateTransactionAmount(int transactionId, BigDecimal amount, BigDecimal commission);

  void updateTransactionConfirmations(int transactionId, int confirmations);

  List<Transaction> findAllByUserWallets(List<Integer> walletIds);

  PagingData<List<Transaction>> findAllByUserWallets(List<Integer> walletIds, int offset, int limit);

  PagingData<List<Transaction>> findAllByUserWallets(final List<Integer> walletIds, final int offset,
                                                     final int limit, final String sortColumn,
                                                     String sortDirection, Locale locale);

  List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale);

  Integer getStatementSize(Integer walletId);

  List<Transaction> getOpenTransactionsByMerchant(Merchant merchant);

  BigDecimal maxAmount();

  BigDecimal maxCommissionAmount();

  void setSourceId(Integer trasactionId, Integer sourceId);

  List<TransactionFlatForReportDto> findAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(String startDate, String endDate, Integer operationType, List<Integer> roleIdList, List<Integer> currencyList, List<String> sourceTypeList);

  List<Transaction> getAllOperationsByUserForPeriod(List<Integer> walletIds, String startDate, String endDate, String sortColumn, String sortDirection);
}
