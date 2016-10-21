package me.exrates.service.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import me.exrates.dao.BTCTransactionDao;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.*;
import me.exrates.service.AlgorithmService;
import me.exrates.service.BitcoinService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.util.BiTuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static me.exrates.model.util.BitCoinUtils.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class BitcoinServiceImpl implements BitcoinService {

    private final Logger LOG = LogManager.getLogger("merchant");

    private final WalletAppKit kit;
    private final ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    private final PendingPaymentDao paymentDao;
    private final TransactionService transactionService;
    private final AlgorithmService algorithmService;
    private final BTCTransactionDao btcTransactionDao;
    private final UserService userService;


    private static final BigDecimal SATOSHI = new BigDecimal(100_000_000L);
    private static final int decimalPlaces = 8;

    private final Function<String, Supplier<IllegalStateException>> throwIllegalStateEx = (address) ->
            () -> new IllegalStateException("Pending payment with address " + address + " is not exist");

    @Autowired
    public BitcoinServiceImpl(final BitcoinWalletAppKit kit,
                              final PendingPaymentDao paymentDao,
                              final TransactionService transactionService,
                              final AlgorithmService algorithmService,
                              final BTCTransactionDao btcTransactionDao,
                              final UserService userService)
    {
        this.kit = kit.kit();
        this.paymentDao = paymentDao;
        this.transactionService = transactionService;
        this.algorithmService = algorithmService;
        this.btcTransactionDao = btcTransactionDao;
        this.userService = userService;
    }

    private String extractRecipientAddress(final List<TransactionOutput> outputs) {
        if (outputs.size() < 1) {
            throw new IllegalArgumentException("List with transaction outputs is empty");
        }
        return outputs.stream()
                .filter(tx -> tx.isMine(kit.wallet()))
                .map(tx -> tx.getScriptPubKey().getToAddress(kit.params(), true).toBase58())
                .findFirst()
                .orElseThrow(IllegalStateException::new); //it will never happen
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void changeTransactionConfidence(final String address, final int confidenceLevel) {
        final PendingPayment payment = paymentDao.findByAddress(address)
                .orElseThrow(throwIllegalStateEx.apply(address));
        transactionService.updateTransactionConfirmation(payment.getInvoiceId(), confidenceLevel);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void approveBitcoinTransaction(final String address, final String hash) {
        final BiTuple<PendingPayment, Transaction> tuple = findByAddress(address);
        final PendingPayment payment = tuple.left;
        final Transaction tx = tuple.right;
        paymentDao.delete(payment.getInvoiceId());
        transactionService.provideTransaction(tx);
        final BTCTransaction BTCtx = new BTCTransaction();
        final BigDecimal amount = compute(tx.getAmount(), tx.getCommissionAmount(), Action.ADD);
        BTCtx.setAmount(amount);
        BTCtx.setTransactionId(tx.getId());
        BTCtx.setHash(hash);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByEmail(auth.getName());
            BTCtx.setAcceptanceUser(user);
        }catch (Exception e){
            LOG.error(e);
        }
        btcTransactionDao.create(BTCtx);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void handleUncommittedTransaction(final Wallet wallet, final org.bitcoinj.core.Transaction tx) {
        final Transaction dbTx = findByAddress(extractRecipientAddress(tx.getOutputs())).right;
        final BigDecimal current = satoshiToBtc(tx.getValue(wallet).getValue());
        final BigDecimal target = compute(dbTx.getAmount(), dbTx.getCommissionAmount(), Action.ADD);
        if (target.compareTo(current) != 0 ) {
            transactionService.updateTransactionAmount(dbTx, current);
        }
    }

    @PostConstruct
    public void init() {
        /*kit.wallet().addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> {
            final List<ListenableFuture<TransactionConfidence>> confirmations = IntStream.rangeClosed(1, 3)
                    .mapToObj(x -> tx.getConfidence().getDepthFuture(x))
                    .collect(toList());
            confirmations.forEach(confidence -> confidence.addListener(() -> {
                try {
                    changeTransactionConfidence(extractRecipientAddress(tx.getOutputs()), confidence.get().getDepthInBlocks());
                } catch (final ExecutionException|InterruptedException e) {
                    LOG.error(e);
                }
            }, pool));

            final ListenableFuture<TransactionConfidence> commit = tx.getConfidence().getDepthFuture(4);
            commit.addListener(() -> {
                try {
                    final String address = extractRecipientAddress(tx.getOutputs());
                    final String txHash = tx.getHashAsString();
                    changeTransactionConfidence(extractRecipientAddress(tx.getOutputs()), commit.get().getDepthInBlocks());
                    approveBitcoinTransaction(address, txHash);
                } catch (final ExecutionException|InterruptedException e) {
                    LOG.error(e);
                }

            }, pool);
            handleUncommittedTransaction(wallet, tx);
        });*/
    }

    @PreDestroy
    public void preDestroy() {
        try {
            pool.awaitTermination(25, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error(e);
        }
    }

    private Address address() {

        boolean isFreshAddress = false;
        Address address = kit.wallet().freshReceiveAddress();

        if (paymentDao.findByAddress(address.toString()).isPresent()){
            final int LIMIT = 100;
            int i = 0;
            while (!isFreshAddress && i++<LIMIT){
                address = kit.wallet().freshReceiveAddress();
                isFreshAddress = !paymentDao.findByAddress(address.toString()).isPresent();
            }
            if (i >= LIMIT){
               throw  new IllegalStateException("Can`t generate fresh address");
            }
        }

        return address;
    }

    private String base58Address() {
        return address().toBase58();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private BiTuple<PendingPayment,Transaction> findByAddress(final String address) {
        final PendingPayment payment = paymentDao.findByAddress(address).orElseThrow(() ->
                new IllegalStateException("Pending payment with address " + address + " is not exist"));
        final Transaction tx = transactionService.findById(payment.getInvoiceId());
        return new BiTuple<>(payment, tx);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PendingPayment createInvoice(CreditsOperation operation) {
        final me.exrates.model.Transaction tx = transactionService.createTransactionRequest(operation);
        final Address address = address();
        final PendingPayment payment = new PendingPayment();
        payment.setAddress(address.toBase58());
        payment.setInvoiceId(tx.getId());
        payment.setTransactionHash(computeTransactionHash(tx, address));
        paymentDao.create(payment);
        return payment;
    }

    private String computeTransactionHash(final me.exrates.model.Transaction tx, Address address) {
        if (isNull(tx) || isNull(tx.getCommission()) || isNull(tx.getCommissionAmount())) {
            throw new IllegalArgumentException("Argument itself or contains null");
        }
        final String target = new StringJoiner(":")
                .add(String.valueOf(tx.getId()))
                .add(address.toBase58())
                .toString();
        return algorithmService.sha256(target);
    }

    @Override
    @Transactional
    public boolean provideTransaction(int id, String hash, BigDecimal amount) {

        Transaction tx = transactionService.findById(id);

        PendingPayment payment = paymentDao.findByInvoiceId(id).orElseThrow(() ->
                new IllegalStateException("Pending payment with invoice_id " + id + " is not exist"));

        try {
            if (tx.getAmount().compareTo(amount) != 0 ) {
                transactionService.updateTransactionAmount(tx, amount);
            }
            approveBitcoinTransaction(payment.getAddress().get(), hash);
        }catch (Exception e){
            LOG.error(e);
            return false;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Transaction, BTCTransaction> getBitcoinTransactions(){
        Merchant merchant = new Merchant();
        merchant.setName("Blockchain");
        List<Transaction> list = transactionService.getOpenTransactionsByMerchant(merchant);
        Map<Transaction,BTCTransaction> map = new LinkedHashMap<>();

        for (Transaction transaction : list){
            map.put(transaction,btcTransactionDao.findByTransactionId(transaction.getId()));
        }

        return map;
    }
}
