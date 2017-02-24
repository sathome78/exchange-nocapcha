package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.ErrorInfoDto;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.model.dto.PendingPaymentSimpleDto;
import me.exrates.service.BitcoinService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.invoice.IllegalInvoiceStatusException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.exception.invoice.RejectedPaymentInvoice;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/bitcoin")
@Log4j2
public class BitcoinController {

  private final BitcoinService bitcoinService;
  private final MerchantService merchantService;
  private final MessageSource messageSource;

  @Autowired
  private LocaleResolver localeResolver;

  @Autowired
  public BitcoinController(final BitcoinService bitcoinService,
                           final MerchantService merchantService,
                           final MessageSource messageSource) {
    this.bitcoinService = bitcoinService;
    this.merchantService = merchantService;
    this.messageSource = messageSource;
  }

  @RequestMapping(value = "/payment/prepare", method = POST)
  public ResponseEntity<Map<String, String>> preparePayment(
      @RequestBody Payment payment,
      Principal principal,
      Locale locale) {
    if (!merchantService.checkInputRequestsLimit(payment.getMerchant(), principal.getName())) {
      Map<String, String> error = new HashMap<>();
      error.put("error", messageSource.getMessage("merchants.InputRequestsLimit", null, locale));
      return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
    String email = principal.getName();
    CreditsOperation creditsOperation = merchantService
        .prepareCreditsOperation(payment, email)
        .orElseThrow(InvalidAmountException::new);
    try {
      PendingPayment pendingPayment = bitcoinService.createInvoice(creditsOperation);
      String notification = merchantService
          .sendDepositNotification(Optional.ofNullable(pendingPayment
                  .getAddress()).orElseThrow(
              () -> new MerchantInternalException("Address not presented"))
              , email, locale, creditsOperation, "merchants.depositNotification.body");

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
      Map<String, String> responseMap = new TreeMap<>();
      responseMap.put("notification", notification);
      responseMap.put("qr", "bitcoin:" + Optional.ofNullable(pendingPayment
          .getAddress()).orElseThrow(
          () -> new MerchantInternalException("Address not presented")) + "?amount="
          + creditsOperation.getAmount().add(creditsOperation.getCommissionAmount()).doubleValue() + "&message=Donation%20for%20project%20Exrates");

      return new ResponseEntity<>(responseMap, HttpStatus.OK);
    } catch (final InvalidAmountException | RejectedPaymentInvoice e) {
      Map<String, String> error = new HashMap<>();
      error.put("error", messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale));
      log.warn(error);
      return new ResponseEntity<>(error, NOT_FOUND);
    } catch (final Exception e) {
      Map<String, String> error = new HashMap<>();
      error.put("error", messageSource.getMessage("merchants.internalError", null, locale));
      log.error(ExceptionUtils.getStackTrace(e));
      return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(value = "/payment/accept", method = GET)
  public RedirectView acceptPayment(
      @RequestParam(name = "id") Integer pendingPaymentId,
      @RequestParam String hash,
      @RequestParam BigDecimal amount,
      Principal principal) throws Exception {
    bitcoinService.provideTransaction(pendingPaymentId, hash, amount, principal.getName());
    return new RedirectView("/2a8fy7b07dxe44/bitcoinConfirmation");
  }

  @RequestMapping(value = "/payment/revoke", method = POST)
  @ResponseBody
  public void confirmInvoice(
      @RequestParam(name = "id") Integer pendingPaymentId,
      HttpServletRequest request) throws Exception {
    try {
      bitcoinService.revoke(pendingPaymentId);
    } catch (IllegalInvoiceStatusException e) {
      throw new IllegalInvoiceStatusException(messageSource.getMessage("merchants.invoice.error.notAllowedOperation", null, localeResolver.resolveLocale(request)));
    } catch (InvoiceNotFoundException e) {
      throw new InvoiceNotFoundException(messageSource.getMessage("merchants.error.invoiceRequestNotFound", null, localeResolver.resolveLocale(request)));
    }
  }

  @RequestMapping(value = "/payment/address", method = GET)
  @ResponseBody
  public PendingPaymentSimpleDto getAddress(
      @RequestParam(name = "id") Integer pendingPaymentId,
      HttpServletRequest request) throws Exception {
    try {
      return bitcoinService.getPendingPaymentSimple(pendingPaymentId);
    } catch (InvoiceNotFoundException e) {
      throw new InvoiceNotFoundException(messageSource.getMessage("merchants.error.invoiceRequestNotFound", null, localeResolver.resolveLocale(request)));
    }
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler({
      IllegalInvoiceStatusException.class})
  @ResponseBody
  public ErrorInfoDto bitcoinErrorNotAcceptableHandler(HttpServletRequest req, Exception exception) {
    log.error("\n\t" + ExceptionUtils.getStackTrace(exception));
    if (exception.getLocalizedMessage() == null || exception.getLocalizedMessage().isEmpty()) {
      return new ErrorInfoDto(exception.getClass().getSimpleName());
    } else {
      return new ErrorInfoDto(exception.getClass().getSimpleName(), exception.getLocalizedMessage());
    }
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({
      InvoiceNotFoundException.class})
  @ResponseBody
  public ErrorInfoDto bitcoinErrorNotFoundHandler(HttpServletRequest req, Exception exception) {
    log.error("\n\t" + ExceptionUtils.getStackTrace(exception));
    if (exception.getLocalizedMessage() == null || exception.getLocalizedMessage().isEmpty()) {
      return new ErrorInfoDto(exception.getClass().getSimpleName());
    } else {
      return new ErrorInfoDto(exception.getClass().getSimpleName(), exception.getLocalizedMessage());
    }
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));
    return new ErrorInfo(req.getRequestURL(), exception);
  }

}
