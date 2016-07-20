package me.exrates.service.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.*;
import me.exrates.model.dto.DataTable;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.vo.CacheData;
import me.exrates.service.*;
import me.exrates.service.exception.TransactionPersistException;
import me.exrates.service.exception.TransactionProvidingException;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.valueOf;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOG = LogManager.getLogger(TransactionServiceImpl.class);
    private static final int decimalPlaces = 8;

    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CompanyWalletService companyWalletService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantService merchantService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Transaction createTransactionRequest(CreditsOperation creditsOperation) {
        final Currency currency = creditsOperation.getCurrency();
        final User user = creditsOperation.getUser();
        final String currencyName = currency.getName();

        CompanyWallet companyWallet = companyWalletService.findByCurrency(currency);
        companyWallet = companyWallet == null ? companyWalletService.create(currency) : companyWallet;

        Wallet userWallet = walletService.findByUserAndCurrency(user, currency);
        userWallet = userWallet == null ? walletService.create(user, currency) : userWallet;

        Transaction transaction = new Transaction();
        transaction.setAmount(creditsOperation.getAmount());
        transaction.setCommissionAmount(creditsOperation.getCommissionAmount());
        transaction.setCommission(creditsOperation.getCommission());
        transaction.setCompanyWallet(companyWallet);
        transaction.setUserWallet(userWallet);
        transaction.setCurrency(currency);
        transaction.setDatetime(LocalDateTime.now());
        transaction.setMerchant(creditsOperation.getMerchant());
        transaction.setOperationType(creditsOperation.getOperationType());
        transaction.setProvided(false);
        transaction.setConfirmation((currencyName).equals("BTC") ? 0 : -1);
        transaction.setSourceType(TransactionSourceType.MERCHANT);
        transaction = transactionDao.create(transaction);
        if (transaction == null) {
            throw new TransactionPersistException("Failed to provide transaction ");
        }
        LOG.info("Transaction created:" + transaction);
        return transaction;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Transaction findById(int id) {
        return transactionDao.findById(id);
    }

    @Override
    public void updateTransactionAmount(final Transaction transaction, final BigDecimal amount) {
        if (transaction.getOperationType() != OperationType.INPUT) {
            throw new IllegalArgumentException("Updating amount only available for INPUT operation");
        }
        final BigDecimal commission = amount
                .multiply(transaction.getCommission().getValue()
                        .divide(BigDecimal.valueOf(100L)).setScale(decimalPlaces, ROUND_HALF_UP));
        final BigDecimal newAmount = amount.subtract(commission).setScale(decimalPlaces, ROUND_HALF_UP);
        transaction.setCommissionAmount(commission);
        transaction.setAmount(newAmount);
        transactionDao.updateTransactionAmount(transaction.getId(), newAmount, commission);
    }

    @Override
    public void updateTransactionConfirmation(final int transactionId, final int confirmations) {
        transactionDao.updateTransactionConfirmations(transactionId, confirmations);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void provideTransaction(Transaction transaction) {
        switch (transaction.getOperationType()) {
            case INPUT:
                walletService.depositActiveBalance(transaction.getUserWallet(), transaction.getAmount());
                companyWalletService.deposit(transaction.getCompanyWallet(), transaction.getAmount(),
                        transaction.getCommissionAmount());
                break;
            case OUTPUT:
                walletService.withdrawReservedBalance(transaction.getUserWallet(), transaction.getAmount().add(transaction.getCommissionAmount()));
                companyWalletService.withdraw(transaction.getCompanyWallet(), transaction.getAmount(),
                        transaction.getCommissionAmount());
                break;
        }
        if (!transactionDao.provide(transaction.getId())) {
            throw new TransactionProvidingException("Failed to provide transaction #" + transaction.getId());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void invalidateTransaction(Transaction transaction) {
        if (!transactionDao.delete(transaction.getId())) {
            throw new TransactionProvidingException("Failed to delete transaction #" + transaction.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAllByUserWallets(List<Integer> userWalletsIds) {
        if (userWalletsIds.size() == 0) {
            return null;
        }
        return transactionDao.findAllByUserWallets(userWalletsIds);
    }

    @Override
    public DataTable<List<OperationViewDto>> showMyOperationHistory(String email, Locale locale, int offset, int limit) {
        final int id = userService.getIdByEmail(email);
        final List<Integer> wallets = walletService.getAllWallets(id).stream()
                .mapToInt(Wallet::getId)
                .boxed()
                .collect(Collectors.toList());
        final DataTable<List<OperationViewDto>> result = new DataTable<>();
        if (wallets.isEmpty()) {
            result.setData(new ArrayList<>());
            return result;
        }
        final PagingData<List<Transaction>> transactions = transactionDao.findAllByUserWallets(wallets, offset, limit);
        final List<OperationViewDto> operationViews = new ArrayList<>();
        for (final Transaction t : transactions.getData()) {
            OperationViewDto view = new OperationViewDto();
            view.setDatetime(t.getDatetime());
            view.setAmount(t.getAmount());
            view.setCommissionAmount(t.getCommissionAmount());
            view.setCurrency(t.getCurrency().getName());
            view.setOperationType(t.getOperationType());
            view.setMerchant(t.getMerchant());
            view.setOrder(t.getOrder());
            view.setStatus(merchantService.resolveTransactionStatus(t, locale));
            operationViews.add(view);
        }
        result.setData(operationViews);
        result.setRecordsFiltered(transactions.getFiltered());
        result.setRecordsTotal(transactions.getTotal());
        return result;
    }

    @Override
    public DataTable<List<OperationViewDto>> showMyOperationHistory(final String email, final Locale locale) {
        return showMyOperationHistory(email, locale, 0, MAX_VALUE);
    }

    @Override
    public DataTable<List<OperationViewDto>> showUserOperationHistory(final int id, final Locale locale) {
        return showMyOperationHistory(userService.getUserById(id).getEmail(), locale);
    }

    @Override
    public DataTable<List<OperationViewDto>> showUserOperationHistory(final int id, final Locale locale, final Map<String, String> viewParams) {
        if (viewParams.containsKey("start") && viewParams.containsKey("length")) {
            return showMyOperationHistory(userService.getUserById(id).getEmail(), locale, valueOf(viewParams.get("start")), valueOf(viewParams.get("length")));
        }
        return showUserOperationHistory(id, locale);
    }

    @Override
    public List<AccountStatementDto> getAccountStatement(CacheData cacheData, Integer walletId, Integer offset, Integer limit, Locale locale) {
        List<AccountStatementDto> result = transactionDao.getAccountStatement(walletId, offset, limit, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<AccountStatementDto>() {{
                add(new AccountStatementDto(false));
            }};
        }
        return result;
    }

    @Override
    public List<Transaction> getInvoiceOpenTransactions(){
        return transactionDao.getInvoiceOpenTransactions();
    }
}
