package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.InvoiceService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.NotApplicableException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Service
@Log4j2
public class InvoiceServiceImpl implements InvoiceService {

  @Autowired
  private MessageSource messageSource;

  @Autowired
  MerchantService merchantService;

  @Override
  @Transactional
  public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
    throw new NotApplicableException("for " + withdrawMerchantOperationDto);
  }

  @Override
  @Transactional
  public Map<String, String> refill(RefillRequestCreateDto request) {
    String toWallet = String.format("%s: %s - %s",
        request.getRefillRequestParam().getRecipientBankName(),
        request.getAddress(),
        request.getRefillRequestParam().getRecipient());
    String message = messageSource.getMessage("merchants.refill.invoice",
        new Object[]{request.getAmount(), toWallet}, request.getLocale());
    return new HashMap<String, String>() {{
      put("message", message);
    }};
  }

  @Override
  public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
    throw new NotApplicableException("for " + params);
  }
}

