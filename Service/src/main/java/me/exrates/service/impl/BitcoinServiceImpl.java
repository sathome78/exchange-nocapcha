package me.exrates.service.impl;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.*;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
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
  @Autowired
  private BitcoinTransactionService bitcoinTransactionService;

  private String walletPassword;

  private String backupFolder;

  private String nodePropertySource;


  public BitcoinServiceImpl(String propertySource) {
    Properties props = new Properties();
    try {
      props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
      this.walletPassword = props.getProperty("wallet.password");
      this.backupFolder = props.getProperty("backup.folder");
      this.nodePropertySource = props.getProperty("node.propertySource");
    } catch (IOException e) {
      LOG.error(e);
    }

  }
  
  @PostConstruct
  void startBitcoin() {
    bitcoinWalletService.initCore(nodePropertySource);
  }


  @Override
  @Transactional
  public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
    throw new NotImplimentedMethod("for " + withdrawMerchantOperationDto);
  }

  @Override
  @Transactional
  public Map<String, String> refill(RefillRequestCreateDto request) {
//    String address = address();
    String address = "some btc address 1";
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
    Currency currency = currencyService.findByName("BTC");
    Merchant merchant = merchantService.findByName("Blockchain");
    if (refillService.existsUnclosedRefillRequestForAddress(address, merchant.getId(), currency.getId())) {
    /*String address = bitcoinWalletService.getNewAddress(walletPassword);

    final List<Integer> unclosedPendingPaymentStatesList = PendingPaymentStatusEnum.getMiddleStatesSet().stream()
        .map(InvoiceStatus::getCode)
        .collect(toList());

    if (paymentDao.existsPendingPaymentWithAddressAndStatus(address, unclosedPendingPaymentStatesList)) {
      */
      final int LIMIT = 2000;
      int i = 0;
      while (!isFreshAddress && i++ < LIMIT) {
        address = bitcoinWalletService.getNewAddress(walletPassword);
        isFreshAddress = !refillService.existsUnclosedRefillRequestForAddress(address, merchant.getId(), currency.getId());
        /*address = bitcoinWalletService.getNewAddress(walletPassword);
        isFreshAddress = !paymentDao.existsPendingPaymentWithAddressAndStatus(address, unclosedPendingPaymentStatesList);*/
      }
      if (i >= LIMIT) {
        throw new IllegalStateException("Can`t generate fresh address");
      }
    }

    return address;
  }

}
