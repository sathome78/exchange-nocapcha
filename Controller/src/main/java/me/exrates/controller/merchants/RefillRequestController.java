package me.exrates.controller.merchants;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestParamsDto;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.RefillRequestLimitForMerchantExceededException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;
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

  @RequestMapping(value = "/refill/request/create", method = POST)
  @ResponseBody
  public Map<String, String> createRefillRequest(
      @ModelAttribute("payment") RefillRequestParamsDto requestParamsDto,
      Principal principal,
      Locale locale) throws UnsupportedEncodingException {
    if (requestParamsDto.getOperationType() != INPUT) {
      throw new IllegalOperationTypeException(requestParamsDto.getOperationType().name());
    }
    RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
    Payment payment = new Payment(INPUT);
    payment.setCurrency(requestParamsDto.getCurrency());
    payment.setMerchant(requestParamsDto.getMerchant());
    payment.setSum(requestParamsDto.getSum().doubleValue());
    CreditsOperation creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName())
        .orElseThrow(InvalidAmountException::new);
    try {
      RefillRequestCreateDto request = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus);
      RedirectView rw = refillService.createRefillRequestAndGetPageOfMerchant(request);
      String url = rw.getUrl().concat("?").concat(
          rw.getAttributesMap().entrySet().stream()
          .map(e->e.getKey()+"="+e.getValue())
          .collect(Collectors.joining("&"))
      );
      return new HashMap<String, String>() {{
        put("redirectionUrl", url);
      }};
    } catch (RefillRequestLimitForMerchantExceededException e) {
      Cookie cookie = new Cookie("errorNoty", URLEncoder.encode(messageSource.getMessage("merchants.InputRequestsLimit", null, locale),"UTF-8"));
      cookie.setPath("/");
      return new HashMap<String, String>() {{
        put("redirectionUrl", "/dashboard");
      }};
    } catch (Exception e) {
      Cookie cookie = new Cookie("errorNoty", URLEncoder.encode(messageSource.getMessage("refill.createError", null, locale),"UTF-8"));
      cookie.setPath("/");
      return new HashMap<String, String>() {{
        put("redirectionUrl", "/dashboard");
      }};
    }
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
