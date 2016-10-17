package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Merchant;
import me.exrates.model.Transaction;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.dto.DataTable;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.enums.TransactionType;
import me.exrates.model.vo.CacheData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionService {

    Transaction createTransactionRequest(CreditsOperation creditsOperation);

    Transaction findById(int id);

    void updateTransactionAmount(Transaction transaction, BigDecimal amount);

    void nullifyTransactionAmountForWithdraw(Transaction transaction);

    void updateTransactionConfirmation(int transactionId, int confirmations);

    void provideTransaction(Transaction transaction);

    void invalidateTransaction(Transaction transaction);

    List<Transaction> findAllByUserWallets(List<Integer> userWalletsIds);


    DataTable<List<OperationViewDto>> showMyOperationHistory(String email, Integer status,
                                                             List<TransactionType> types, List<Integer> merchantIds,
                                                             String dateFrom, String dateTo,
                                                             BigDecimal fromAmount, BigDecimal toAmount,
                                                             BigDecimal fromCommissionAmount, BigDecimal toCommissionAmount,
                                                             int offset, int limit,
                                                             String sortColumn, String sortDirection, Locale locale);

    DataTable<List<OperationViewDto>> showMyOperationHistory(String email, Locale locale, int offset, int limit);

    DataTable<List<OperationViewDto>> showMyOperationHistory(String email, Locale locale);

    DataTable<List<OperationViewDto>> showUserOperationHistory(int id, Locale locale);

    DataTable<List<OperationViewDto>> showUserOperationHistory(int id, Integer status,
                                                               List<TransactionType> types, List<Integer> merchantIds,
                                                               String dateFrom, String dateTo,
                                                               BigDecimal fromAmount, BigDecimal toAmount,
                                                               BigDecimal fromCommissionAmount, BigDecimal toCommissionAmount, Locale locale, Map<String, String> viewParams);

    List<AccountStatementDto> getAccountStatement (CacheData cacheData, Integer walletId, Integer offset, Integer limit, Locale locale);

    DataTable<List<AccountStatementDto>> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale);

    List<Transaction> getOpenTransactionsByMerchant(Merchant merchant);

    BigDecimal maxAmount();

    BigDecimal maxCommissionAmount();
}
