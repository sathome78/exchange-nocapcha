package me.exrates.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import me.exrates.dao.EDCAccountDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.*;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@PropertySource({"classpath:edc_cli_wallet.properties", "classpath:/merchants/edcmerchant.properties"})
public class EDCServiceImpl implements EDCService {

  private @Value("${edcmerchant.token}") String token;
  private @Value("${edcmerchant.main_account}") String main_account;
  private @Value("${edcmerchant.hook}") String hook;

  private final Logger LOG = LogManager.getLogger("merchant");

  @Autowired
  TransactionService transactionService;

  @Autowired
  EDCAccountDao edcAccountDao;

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
        new Object[]{address}, request.getLocale());
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
    Currency currency = currencyService.findByName("EDR");
    Merchant merchant = merchantService.findByName("EDC");
    BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(params.get("amount")));
    RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
        .address(address)
        .merchantId(merchant.getId())
        .currencyId(currency.getId())
        .amount(amount)
        .merchantTransactionId(StringUtils.isEmpty(merchantTransactionId) ? hash : merchantTransactionId)
        .toMainAccountTransferringNeeded(merchant.getToMainAccountTransferringNeeded())
        .build();
    try {
      refillService.autoAcceptRefillRequest(requestAcceptDto);
    } catch (RefillRequestAppropriateNotFoundException e) {
      LOG.debug("RefillRequestNotFountException: " + params);
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
      returnResponse = client
          .newCall(request)
          .execute()
          .body()
          .string();
    } catch (IOException e) {
      throw new MerchantInternalException(e);
    }

    JsonParser parser = new JsonParser();
    JsonArray jsonArray = parser.parse(returnResponse).getAsJsonArray();

    for (JsonElement element : jsonArray) {
      if (element.getAsJsonObject().get("id").getAsString().equals(params.get("id"))) {
        if (element.getAsJsonObject().get("amount").getAsString().equals(params.get("amount"))) {
          return;
        }
      }
    }
    return;
//    throw new RefillRequestFakePaymentReceivedException(params.toString());
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
