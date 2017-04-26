package me.exrates.controller.merchants;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.service.*;
import me.exrates.model.CreditsOperation;
import me.exrates.model.InvoiceBank;
import me.exrates.model.Payment;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.RefillRequestParamsDto;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * created by ValkSam
 */
@Controller
public class RefillRequestController {

  private static final Logger log = LogManager.getLogger("refill");

  @Autowired
  private MessageSource messageSource;

  @Autowired
  RefillService refillService;

  @Autowired
  UserService userService;

  @Autowired
  MerchantService merchantService;

  @Autowired
  private InputOutputService inputOutputService;

  @RequestMapping(value = "/refill/request/create", method = POST)
  @ResponseBody
  public Map<String, String> createRefillRequest(
      @RequestBody RefillRequestParamsDto requestParamsDto,
      Principal principal,
      Locale locale) throws UnsupportedEncodingException {
    if (requestParamsDto.getOperationType() != INPUT) {
      throw new IllegalOperationTypeException(requestParamsDto.getOperationType().name());
    }
    if (!refillService.checkInputRequestsLimit(requestParamsDto.getCurrency(), principal.getName())) {
      throw new RequestLimitExceededException(messageSource.getMessage("merchants.InputRequestsLimit", null, locale));
    }
    RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
    Payment payment = new Payment(INPUT);
    payment.setCurrency(requestParamsDto.getCurrency());
    payment.setMerchant(requestParamsDto.getMerchant());
    payment.setSum(requestParamsDto.getSum().doubleValue());
    CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, principal.getName())
        .orElseThrow(InvalidAmountException::new);
    RefillRequestCreateDto request = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
    return refillService.createRefillRequest(request, locale);
  }

  @RequestMapping(value = "/refill/request/revoke", method = POST)
  @ResponseBody
  public void revokeWithdrawRequest(
      @RequestParam Integer id) {
    refillService.revokeRefillRequest(id);
  }

  @RequestMapping(value = "/refill/request/info", method = GET)
  @ResponseBody
  public RefillRequestFlatDto getInfoRefill(
      @RequestParam Integer id) {
    return refillService.getFlatById(id);
  }

  @RequestMapping(value = "/refill/request/confirm", method = POST)
  @ResponseBody
  public void confirmWithdrawRequest(
      InvoiceConfirmData invoiceConfirmData,
      Locale locale) {
    refillService.confirmRefillRequest(invoiceConfirmData, locale);
  }

  @RequestMapping(value = "/refill/banks", method = GET)
  @ResponseBody
  public List<InvoiceBank> getBankListForCurrency(
      @RequestParam Integer currencyId) {
    return refillService.findBanksForCurrency(currencyId);
  }

  @RequestMapping(value = "/refill/commission", method = GET)
  @ResponseBody
  public Map<String, String> getCommissions(
      @RequestParam("amount") BigDecimal amount,
      @RequestParam("currency") Integer currencyId,
      @RequestParam("merchant") Integer merchantId,
      Principal principal,
      Locale locale) {
    Integer userId = userService.getIdByEmail(principal.getName());
    return refillService.correctAmountAndCalculateCommission(userId, amount, currencyId, merchantId, locale);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/refill/take", method = POST)
  @ResponseBody
  public void takeToWork(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    refillService.takeInWorkRefillRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/refill/return", method = POST)
  @ResponseBody
  public void returnFromWork(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    refillService.returnFromWorkRefillRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/refill/decline", method = POST)
  @ResponseBody
  public void decline(
      @RequestParam Integer id,
      @RequestParam String comment,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    refillService.declineRefillRequest(id, requesterAdminId, comment);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/refill/accept", method = POST)
  @ResponseBody
  public void accept(
      @RequestParam(required = true) Integer id,
      @RequestParam(required = true) BigDecimal amount,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
        .requestId(id)
        .amount(amount)
        .requesterAdminId(requesterAdminId)
        .build();
    refillService.acceptRefillRequest(requestAcceptDto);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(InvoiceNotFoundException.class)
  @ResponseBody
  public ErrorInfo NotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
    log.error(exception);
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler({
      InvoiceActionIsProhibitedForCurrencyPermissionOperationException.class,
      InvoiceActionIsProhibitedForNotHolderException.class
  })
  @ResponseBody
  public ErrorInfo ForbiddenExceptionHandler(HttpServletRequest req, Exception exception) {
    log.error(exception);
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler({
      NotEnoughUserWalletMoneyException.class,
  })
  @ResponseBody
  public ErrorInfo NotAcceptableExceptionHandler(HttpServletRequest req, Exception exception) {
    log.error(exception);
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));
    return new ErrorInfo(req.getRequestURL(), exception);
  }

}
