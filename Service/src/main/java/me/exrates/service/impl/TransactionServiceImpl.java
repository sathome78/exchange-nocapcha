package me.exrates.service.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.CompanyWallet;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.OperationViewDtoComparator;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.MerchantService;
import me.exrates.service.OrderService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.TransactionPersistException;
import me.exrates.service.exception.TransactionProvidingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class TransactionServiceImpl implements TransactionService {

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

    private static final Logger LOG = LogManager.getLogger(TransactionServiceImpl.class);
    private static final MathContext MATH_CONTEXT = new MathContext(9, RoundingMode.CEILING);

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Transaction createTransactionRequest(CreditsOperation creditsOperation) {
        final Currency currency = creditsOperation.getCurrency();
        final User user = creditsOperation.getUser();
        final String currencyName = currency.getName();

        CompanyWallet companyWallet = companyWalletService.findByCurrency(currency);
        companyWallet = companyWallet == null ? companyWalletService.create(currency) : companyWallet;

        Wallet userWallet = walletService.findByUserAndCurrency(user,currency);
        userWallet = userWallet == null ? walletService.create(user,currency) : userWallet;

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
        transaction = transactionDao.create(transaction);
        if (transaction==null) {
            throw new TransactionPersistException("Failed to provide transaction ");
        }
        LOG.info("Transaction created:"+transaction);
        return transaction;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,readOnly = true)
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
                        .divide(BigDecimal.valueOf(100L),MATH_CONTEXT));
        final BigDecimal newAmount = amount.subtract(commission,MATH_CONTEXT);
        transaction.setCommissionAmount(commission);
        transaction.setAmount(newAmount);
        transactionDao.updateTransactionAmount(transaction.getId(), newAmount, commission);
    }

    @Override
    public void updateTransactionConfirmation(final int transactionId, final int confirmations) {
        transactionDao.updateTransactionConfirmations(transactionId ,confirmations);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void provideTransaction(Transaction transaction) {
        switch (transaction.getOperationType()) {
            case INPUT :
                walletService.depositActiveBalance(transaction.getUserWallet(),transaction.getAmount());
                companyWalletService.deposit(transaction.getCompanyWallet(),transaction.getAmount(),
                    transaction.getCommissionAmount());
                break;
            case OUTPUT:
                walletService.withdrawReservedBalance(transaction.getUserWallet(),transaction.getAmount().add(transaction.getCommissionAmount()));
                companyWalletService.withdraw(transaction.getCompanyWallet(),transaction.getAmount(),
                    transaction.getCommissionAmount());
                break;
        }
        if (!transactionDao.provide(transaction.getId())) {
            throw new TransactionProvidingException("Failed to provide transaction #"+transaction.getId());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void invalidateTransaction(Transaction transaction) {
        if (!transactionDao.delete(transaction.getId())) {
            throw new TransactionProvidingException("Failed to delete transaction #"+transaction.getId());
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
    public List<OperationViewDto> showMyOperationHistory(String email, Locale locale) {
        int id = userService.getIdByEmail(email);
        final List<Integer> collect = walletService.getAllWallets(id)
            .stream()
            .mapToInt(Wallet::getId)
            .boxed()
            .collect(Collectors.toList());
        final List<Transaction> allByUserId = findAllByUserWallets(collect);
        LOG.info(allByUserId);
        List<OperationViewDto> list = new ArrayList<>();
        if (allByUserId != null) {
            allByUserId
                .stream()
                    .forEach(t -> {
                        OperationViewDto view = new OperationViewDto();
                        view.setDatetime(t.getDatetime());
                        view.setAmount(t.getAmount());
                        view.setCommissionAmount(t.getCommissionAmount());
                        view.setCurrency(t.getCurrency().getName());
                        view.setOperationType(t.getOperationType());
                        view.setMerchant(t.getMerchant());
                        view.setOrder(t.getOrder());
                        view.setStatus(merchantService.resolveTransactionStatus(t, locale));
                        list.add(view);
                });
        }
        Collections.sort(list, new OperationViewDtoComparator());
        return list;
    }

    @Override
    public List<OperationViewDto> showUserOperationHistory(int id, Locale locale) {
        return showMyOperationHistory(userService.getUserById(id).getEmail(), locale);
    }
}
