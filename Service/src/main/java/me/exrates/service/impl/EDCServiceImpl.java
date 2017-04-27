package me.exrates.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.*;
import me.exrates.dao.EDCAccountDao;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.*;
import me.exrates.model.dto.PendingPaymentSimpleDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.service.*;
import me.exrates.service.exception.RefillRequestFakePaymentReceivedException;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.BiTuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.springframework.transaction.annotation.Propagation.NESTED;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource({"classpath:edc_cli_wallet.properties", "classpath:/merchants/edcmerchant.properties"})
public class EDCServiceImpl implements EDCService {

  private @Value("${edcmerchant.token}") String token;
  private @Value("${edcmerchant.main_account}") String main_account;
  private @Value("${edcmerchant.hook}") String hook;

  private @Value("${edc.blockchain.host_delayed}") String RPC_URL_DELAYED;
  private @Value("${edc.blockchain.host_fast}") String RPC_URL_FAST;
  private @Value("${edc.account.registrar}") String REGISTRAR_ACCOUNT;
  private @Value("${edc.account.referrer}") String REFERRER_ACCOUNT;
  private @Value("${edc.account.main}") String MAIN_ACCOUNT;
  private @Value("${edc.account.main.private.key}") String MAIN_ACCOUNT_PRIVATE_KEY;
  private final String PENDING_PAYMENT_HASH = "1fc3403096856798ab8992f73f241334a4fe98ce";

  private final Logger LOG = LogManager.getLogger("merchant");

  private final BigDecimal BTS = new BigDecimal(1000L);
  private final int DEC_PLACES = 2;

  private final OkHttpClient HTTP_CLIENT = new OkHttpClient();
  private final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

  private final ConcurrentMap<String, PendingPaymentSimpleDto> pendingPayments = new ConcurrentHashMap<>();
  private final BlockingQueue<String> rawTransactions = new LinkedBlockingQueue<>();
  private final BlockingQueue<BiTuple<String, String>> incomingPayments = new LinkedBlockingQueue<>();
  private final ExecutorService workers = Executors.newFixedThreadPool(2);
  private volatile boolean isRunning = true;

  private final String ACCOUNT_PREFIX = "ex1f";
  private final String REGISTER_NEW_ACCOUNT_RPC = "{\"method\":\"register_account\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", 0, \"true\"], \"id\":%s}";
  private final String NEW_KEY_PAIR_RPC = "{\"method\": \"suggest_brain_key\", \"jsonrpc\": \"2.0\", \"params\": [], \"id\": %d}";
  private final String IMPORT_KEY = "{\"method\": \"import_key\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\",\"%s\"], \"id\": %s}";
  private final String TRANSFER_EDC = "{\"method\":\"transfer\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"true\"], \"id\":%s}";
  private final Pattern pattern = Pattern.compile("\"brain_priv_key\":\"([\\w\\s]+)+\",\"wif_priv_key\":\"(\\S+)\",\"pub_key\":\"(\\S+)\"");

  private volatile boolean debugLog = true;

  @Autowired
  TransactionService transactionService;

  @Autowired
  EDCAccountDao edcAccountDao;

  @Autowired
  PendingPaymentDao paymentDao;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private RefillService refillService;

  @Autowired
  private MerchantService merchantService;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  EDCServiceNode edcServiceNode;

  @Override
  public void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
    edcServiceNode.transferFromMainAccount(
        withdrawMerchantOperationDto.getAccountTo(),
        withdrawMerchantOperationDto.getAmount());
  }

  @Override
  public Map<String, String> refill(RefillRequestCreateDto request) {
    String address = getAddress();
    String message = messageSource.getMessage("merchants.refill.edr",
        new Object[]{request.getAmount(), address}, request.getLocale());
    return new HashMap<String, String>() {{
      put("address", address);
      put("message", message);
      put("qr", address);
    }};
  }

  @Override
  public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
    checkTransactionByHistory(params);
    String merchantTransactionId = params.get("id");
    String address = params.get("address");
    String hash = params.get("hash");
    me.exrates.model.Currency currency = currencyService.findByName("EDR");
    Merchant merchant = merchantService.findByName("EDC");
    BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(params.get("amount")));
    RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
        .address(address)
        .merchantId(merchant.getId())
        .currencyId(currency.getId())
        .amount(amount)
        .merchantTransactionId(merchantTransactionId)
        .hash(hash)
        .build();
    try {
      refillService.autoAcceptRefillRequest(requestAcceptDto);
    } catch (RefillRequestAppropriateNotFoundException e) {
      LOG.debug("RefillRequestNotFountException: "+params);
      Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
      requestAcceptDto.setRequestId(requestId);
      refillService.autoAcceptRefillRequest(requestAcceptDto);
    }
  }

  private void checkTransactionByHistory(Map<String, String> params) {
    final OkHttpClient client = new OkHttpClient();
    final Request request = new Request.Builder()
        .url("https://receive.edinarcoin.com/history/" + token + "/" + params.get("address"))
        .build();
    final String returnResponse;

    try {
      returnResponse =client
          .newCall(request)
          .execute()
          .body()
          .string();
    } catch (IOException e) {
      throw new MerchantInternalException(e);
    }

    JsonParser parser = new JsonParser();
    JsonArray jsonArray = parser.parse(returnResponse).getAsJsonArray();

    for (JsonElement element : jsonArray){
      if (element.getAsJsonObject().get("id").getAsString().equals(params.get("id"))){
        if (element.getAsJsonObject().get("amount").getAsString().equals(params.get("amount"))){
          return;
        }
      }
    }
    throw new RefillRequestFakePaymentReceivedException(params.toString());
  }

  private String getAddress() {
    final OkHttpClient client = new OkHttpClient();
    client.setReadTimeout(60, TimeUnit.SECONDS);
    final FormEncodingBuilder formBuilder = new FormEncodingBuilder();
    formBuilder.add("account", main_account);
    formBuilder.add("hook", hook);
    final Request request = new Request.Builder()
        .url("https://receive.edinarcoin.com/new-account/" + token)
        .post(formBuilder.build())
        .build();
    final String returnResponse;
    try {
      returnResponse = client
          .newCall(request)
          .execute()
          .body()
          .string();
    } catch (IOException e) {
      throw new MerchantInternalException(e);
    }
    JsonParser parser = new JsonParser();
    JsonObject object = parser.parse(returnResponse).getAsJsonObject();
    return object.get("address").getAsString();
  }



}
