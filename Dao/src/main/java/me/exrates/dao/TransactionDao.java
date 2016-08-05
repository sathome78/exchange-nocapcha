package me.exrates.dao;

import me.exrates.model.PagingData;
import me.exrates.model.Transaction;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionDao {

    Transaction create(Transaction transaction);

    Transaction findById(int id);

    boolean provide(int id);

    boolean delete(int id);

    void updateTransactionAmount(int transactionId, BigDecimal amount, BigDecimal commission);

    void updateTransactionConfirmations(int transactionId, int confirmations);

    List<Transaction> findAllByUserWallets(List<Integer> walletIds);

    PagingData<List<Transaction>> findAllByUserWallets(List<Integer> walletIds, int offset, int limit);

    PagingData<List<Transaction>> findAllByUserWallets(final List<Integer> walletIds, final int offset,
                                                       final int limit, final String searchValue, String sortColumn,
                                                       String sortDirection, Locale locale);

    List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale);

    List<Transaction> getInvoiceOpenTransactions();
}
