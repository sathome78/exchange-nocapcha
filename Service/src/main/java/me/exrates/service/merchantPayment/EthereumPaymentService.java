package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.service.EthereumService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Created by OLEG on 28.04.2017.
 */
@Component("EthereumPaymentService")
public class EthereumPaymentService implements MerchantPaymentService {
  
  private static final Logger LOG = LogManager.getLogger("merchant");
  
  @Autowired
  private MerchantService merchantService;
  
  @Autowired
  private EthereumService ethereumService;
  
  private MerchantInputResponseDto preparePayment(String email, Payment payment, Locale locale) {
    /*final CreditsOperation creditsOperation = merchantService
            .prepareCreditsOperation(payment, email)
            .orElseThrow(InvalidAmountException::new);
      final String account = ethereumService.createAddress(creditsOperation);
      final String notification = merchantService
              .sendDepositNotification(account,email ,locale, creditsOperation, "merchants.depositNotification.body");
     MerchantInputResponseDto dto = new MerchantInputResponseDto();
     dto.setData(notification);
     dto.setQr(account + "/"
             + creditsOperation.getAmount().add(creditsOperation.getCommissionAmount()).doubleValue()
             + "/image.png");
     dto.setType(MerchantApiResponseType.NOTIFY);
     dto.setWalletNumber(account);
     return dto;*/
    return null;
  }
  
  @Override
  public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
    return Collections.EMPTY_MAP;
  }
}
