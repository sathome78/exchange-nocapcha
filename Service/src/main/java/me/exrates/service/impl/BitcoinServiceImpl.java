package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.model.dto.btcTransactionFacade.BtcTransactionDto;
import me.exrates.service.*;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
@Log4j2(topic = "bitcoin_core")
@PropertySource(value = {"classpath:/job.properties"})
public class BitcoinServiceImpl implements BitcoinService {

  private final Logger LOG = LogManager.getLogger("merchant");

  @Value("${btcInvoice.blockNotifyUsers}")
  private Boolean BLOCK_NOTIFYING;

  @Autowired
  private RefillService refillService;
  @Autowired
  private CurrencyService currencyService;
  @Autowired
  private MerchantService merchantService;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private CoreWalletService bitcoinWalletService;

  private String walletPassword;

  private String backupFolder;

  private String nodePropertySource;
  
  private String merchantName;
  
  private String currencyName;
  
  private Integer minConfirmations;


  public BitcoinServiceImpl(String propertySource, String merchantName, String currencyName, Integer minConfirmations) {
    Properties props = new Properties();
    try {
      props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
      this.walletPassword = props.getProperty("wallet.password");
      this.backupFolder = props.getProperty("backup.folder");
      this.nodePropertySource = props.getProperty("node.propertySource");
      this.merchantName = merchantName;
      this.currencyName = currencyName;
      this.minConfirmations = minConfirmations;
    } catch (IOException e) {
      LOG.error(e);
    }

  }
  
  @PostConstruct
  void startBitcoin() {
    bitcoinWalletService.initCoreClient(nodePropertySource);
    bitcoinWalletService.initBtcdDaemon(this::onIncomingBlock, this::onIncomingPayment, this::onInstantSend);
  }


  @Override
  @Transactional
  public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
    throw new NotImplimentedMethod("for " + withdrawMerchantOperationDto);
  }

  @Override
  @Transactional
  public Map<String, String> refill(RefillRequestCreateDto request) {
    String address = address();
    String message = messageSource.getMessage("merchants.refill.btc",
        new Object[]{request.getAmount(), address}, request.getLocale());
    return new HashMap<String, String>() {{
      put("address", address);
      put("message", message);
      put("qr", address);
    }};
  }

  @Override
  public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
    throw new NotImplimentedMethod("for " + params);
  }

  private String address() {
    boolean isFreshAddress = false;
    String address = bitcoinWalletService.getNewAddress(walletPassword);
    Currency currency = currencyService.findByName(currencyName);
    Merchant merchant = merchantService.findByName(merchantName);
    if (refillService.existsUnclosedRefillRequestForAddress(address, merchant.getId(), currency.getId())) {
      final int LIMIT = 2000;
      int i = 0;
      while (!isFreshAddress && i++ < LIMIT) {
        address = bitcoinWalletService.getNewAddress(walletPassword);
        isFreshAddress = !refillService.existsUnclosedRefillRequestForAddress(address, merchant.getId(), currency.getId());
      }
      if (i >= LIMIT) {
        throw new IllegalStateException("Can`t generate fresh address");
      }
    }

    return address;
  }
  
  private void onIncomingPayment(BtcTransactionDto btcTransactionDto) {
    Merchant merchant = merchantService.findByName(merchantName);
    Currency currency = currencyService.findByName(currencyName);
    Optional<BtcTransactionDto> targetTxResult = bitcoinWalletService.handleTransactionConflicts(btcTransactionDto.getTxId());
    if (targetTxResult.isPresent()) {
      BtcTransactionDto targetTx = targetTxResult.get();
      targetTx.getDetails().stream().filter(payment -> "RECEIVE".equalsIgnoreCase( payment.getCategory()))
              .forEach(payment -> {
                log.debug("Payment " + payment);
                  if (targetTx.getConfirmations() == 0) {
                    Optional<Integer> refillRequestIdResult = refillService.getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(payment.getAddress(),
                            merchant.getId(), currency.getId());
                    Integer requestId = refillRequestIdResult.orElseGet(() ->
                            refillService.createRefillRequestByFact(RefillRequestAcceptDto.builder()
                                    .address(payment.getAddress())
                                    .amount(payment.getAmount())
                                    .merchantId(merchant.getId())
                                    .currencyId(currency.getId())
                                    .merchantTransactionId(targetTx.getTxId()).build()));
                    try {
                      refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                              .requestId(requestId)
                              .merchantId(merchant.getId())
                              .currencyId(currency.getId())
                              .address( payment.getAddress())
                              .amount(payment.getAmount())
                              .hash(targetTx.getTxId()).build());
                    } catch (RefillRequestAppropriateNotFoundException e) {
                      log.error(e);
                    }
                  }
              });
    } else {
      log.error("Invalid transaction");
    }
  
  }
  
  private void onIncomingBlock(String blockHash) {
    
    Merchant merchant = merchantService.findByName(merchantName);
    Currency currency = currencyService.findByName(currencyName);
    
    List<RefillRequestFlatDto> btcRefillRequests = refillService.getInExamineByMerchantIdAndCurrencyIdList(merchant.getId(), currency.getId());
    btcRefillRequests.forEach(log::debug);
    List<RefillRequestSetConfirmationsNumberDto> paymentsToUpdate = new ArrayList<>();
    log.debug("Start retrieving TXs");
    btcRefillRequests.stream().filter(request -> StringUtils.isNotEmpty(request.getMerchantTransactionId())).forEach(request -> {
      log.debug("Retrieving tx from blockchain!");
        Optional<BtcTransactionDto> txResult = bitcoinWalletService.handleTransactionConflicts(request.getMerchantTransactionId());
        if (txResult.isPresent()) {
          BtcTransactionDto tx = txResult.get();
          log.debug("Target tx: " + tx.getTxId());
          /*if (!request.getMerchantTransactionId().equals(tx.getTxId())) {
            bitcoinTransactionService.updatePendingPaymentHash(request.getInvoiceId(), tx.getTxId());
          }*/
          tx.getDetails().stream().filter(paymentOverview -> request.getAddress().equals(paymentOverview.getAddress()))
                  .peek(log::debug)
                  .findFirst().ifPresent(paymentOverview -> {
                    log.debug("Adding payment to list: " + paymentOverview);
                    paymentsToUpdate.add(RefillRequestSetConfirmationsNumberDto.builder()
                            .address(paymentOverview.getAddress())
                            .amount(paymentOverview.getAmount())
                            .currencyId(currency.getId())
                            .merchantId(merchant.getId())
                            .requestId(request.getId())
                            .confirmations(tx.getConfirmations())
                            .hash(tx.getTxId()).build());
                  }
                  );
          
        } else {
          log.error("No valid transactions available!");
        }
      
    });
  
    log.debug("Start updating payments in DB");
    paymentsToUpdate.forEach(payment -> {
      log.debug(String.format("Payment to update: %s", payment));
      changeConfirmationsOrProvide(payment);
    });
  }
  
  private void onInstantSend(BtcTransactionDto transactionDto) {
  
  }
  
  
  
  
  private void changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto dto) {
    try {
      refillService.setConfirmationCollectedNumber(dto);
      if (dto.getConfirmations() >= CONFIRMATION_NEEDED_COUNT) {
        log.debug("Providing transaction!");
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(dto.getRequestId())
                .address(dto.getAddress())
                .amount(dto.getAmount())
                .currencyId(dto.getCurrencyId())
                .merchantId(dto.getMerchantId())
                .merchantTransactionId(dto.getHash())
                .build();
        refillService.autoAcceptRefillRequest(requestAcceptDto);
      }
    } catch (RefillRequestAppropriateNotFoundException e) {
      log.error(e);
    }
    
  }
  
  @Override
  @Scheduled(initialDelay = 5 * 60000, fixedDelay = 12 * 60 * 60000)
  public void backupWallet() {
    bitcoinWalletService.backupWallet(backupFolder);
  }
  
  @Override
  public BtcWalletInfoDto getWalletInfo() {
    return bitcoinWalletService.getWalletInfo();
  }
  
  @Override
  public List<BtcTransactionHistoryDto> listAllTransactions() {
    return bitcoinWalletService.listAllTransactions();
  }
  
  @Override
  public BigDecimal estimateFee(int blockCount) {
    return bitcoinWalletService.estimateFee(blockCount);
  }
  
  @Override
  public BigDecimal getActualFee() {
    return bitcoinWalletService.getActualFee();
  }
  
  @Override
  public void setTxFee(BigDecimal fee) {
    bitcoinWalletService.setTxFee(fee);
  }
  
  @Override
  public void submitWalletPassword(String password) {
    bitcoinWalletService.submitWalletPassword(password);
  }
  
  @Override
  public String sendToMany(Map<String, BigDecimal> payments) {
    return bitcoinWalletService.sendToMany(payments);
  }

}
