package me.exrates.service.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;
import me.exrates.service.exception.TransactionPersistException;
import me.exrates.service.exception.TransactionProvidingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Transaction createTransactionRequest(CreditsOperation creditsOperation) {
        final Currency currency = creditsOperation.getCurrency();
        final User user = creditsOperation.getUser();

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

        transaction = transactionDao.create(transaction);
        if (transaction==null) {
            throw new TransactionPersistException("Failed to provide transaction ");
        }
        return transaction;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,readOnly = true)
    public Transaction findById(int id) {
        return transactionDao.findById(id);
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
                walletService.withdrawActiveBalance(transaction.getUserWallet(),transaction.getAmount());
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
        if (userWalletsIds.size()==0) {
            return null;
        }
        return transactionDao.findAllByUserWallets(userWalletsIds);
    }

    @Override
    public List<OperationView> showMyOperationHistory(String email) {
    	int id = userService.getIdByEmail(email);
    	final List<Integer> collect = walletService.getAllWallets(id)
                 .stream()
                 .mapToInt(Wallet::getId)
                 .boxed()
                 .collect(Collectors.toList());
         final List<Transaction> allByUserId = findAllByUserWallets(collect);
         Map<String, List<Order>> orderMap = orderService.getMyOrders(email);
         List<Order> orderList = new ArrayList<Order>();
         orderList.addAll(orderMap.get("sell"));
         orderList.addAll(orderMap.get("buy"));
         List<OperationView> list = new ArrayList<OperationView>();
         if(orderList.size() > 0 ) {
        	 for(Order order : orderList) {
	         	OperationView view = new OperationView();
	         	LocalDateTime datetime = order.getDateFinal();
	         	if(order.getDateCreation().isAfter(order.getDateFinal())) {
	         		datetime = order.getDateCreation();
	         	}
	         	view.setDatetime(datetime);
	         	view.setAmount(order.getAmountSell());
	         	view.setAmountBuy(order.getAmountBuy());
	         	BigDecimal commissionAmount = order.getCommissionAmountBuy();
	         	if(order.getOperationType().equals(OperationType.SELL)) {
	         		commissionAmount = order.getCommissionAmountSell();
	         	}
	         	view.setCommissionAmount(commissionAmount);
	         	view.setCurrency(order.getCurrencySellString());
	         	view.setCurrencyBuy(order.getCurrencyBuyString());
	         	view.setOperationType(order.getOperationType());
	         	view.setOrderStatus(order.getStatus());
	         	list.add(view);
        	 }
         }
         if (allByUserId != null) {
	         for(Transaction t : allByUserId) {
	         	OperationView view = new OperationView();
	         	view.setDatetime(t.getDatetime());
	         	view.setAmount(t.getAmount());
	         	view.setCommissionAmount(t.getCommissionAmount());
	         	view.setCurrency(t.getCurrency().getName());
	         	view.setOperationType(t.getOperationType());
	         	view.setMerchant(t.getMerchant());
	         	list.add(view);
	         }
         }
         Collections.sort(list, new OperationViewComparator());
         return list;
    }
}