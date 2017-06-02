package me.exrates.controller.merchants;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.TransferRequestParamsDto;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
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
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.OperationType.USER_TRANSFER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * created by ValkSam
 */
@Controller
public class TransferRequestController {

  private static final Logger log = LogManager.getLogger("transfer");

  @Autowired
  private MessageSource messageSource;

  @Autowired
  TransferService transferService;

  @Autowired
  UserService userService;

  @Autowired
  MerchantService merchantService;

  @Autowired
  private InputOutputService inputOutputService;

  @RequestMapping(value = "/transfer/request/create", method = POST)
  @ResponseBody
  public Map<String, Object> createRefillRequest(
      @RequestBody TransferRequestParamsDto requestParamsDto,
      Principal principal,
      Locale locale) throws UnsupportedEncodingException {
    if (requestParamsDto.getOperationType() != USER_TRANSFER) {
      throw new IllegalOperationTypeException(requestParamsDto.getOperationType().name());
    }
    TransferStatusEnum beginStatus = (TransferStatusEnum) RefillStatusEnum.getBeginState();
    Payment payment = new Payment(requestParamsDto.getOperationType());
    payment.setCurrency(requestParamsDto.getCurrency());
    payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
    payment.setRecipient(requestParamsDto.getRecipient());
    CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, principal.getName())
        .orElseThrow(InvalidAmountException::new);
    TransferRequestCreateDto request = new TransferRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
    return transferService.createTransferRequest(request);
  }

  @RequestMapping(value = "/transfer/request/revoke", method = POST)
  @ResponseBody
  public void revokeWithdrawRequest(
      @RequestParam Integer id) {
    transferService.revokeTransferRequest(id);
  }

  @RequestMapping(value = "/transfer/request/info", method = GET)
  @ResponseBody
  public TransferRequestFlatDto getInfoRefill(
      @RequestParam Integer id) {
    return transferService.getFlatById(id);
  }

  @RequestMapping(value = "/transfer/commission", method = GET)
  @ResponseBody
  public Map<String, String> getCommissions(
      @RequestParam("amount") BigDecimal amount,
      @RequestParam("currency") Integer currencyId,
      @RequestParam("merchant") Integer merchant,
      Principal principal,
      Locale locale) {
    Integer userId = userService.getIdByEmail(principal.getName());
    return transferService.correctAmountAndCalculateCommissionPreliminarily(userId, amount, currencyId, merchant, locale);
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
