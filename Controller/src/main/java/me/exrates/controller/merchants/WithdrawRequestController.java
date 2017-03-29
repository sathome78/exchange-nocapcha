package me.exrates.controller.merchants;

import me.exrates.controller.annotation.FinPassCheck;
import me.exrates.controller.exception.CheckFinPassException;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.WithdrawRequestsAdminTableDto;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.WithdrawService;
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
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * created by ValkSam
 */
@Controller
public class WithdrawRequestController {

  private static final Logger log = LogManager.getLogger("withdraw");

  @Autowired
  private MessageSource messageSource;

  @Autowired
  WithdrawService withdrawService;

  @Autowired
  UserService userService;

  @Autowired
  MerchantService merchantService;

  @FinPassCheck
  @RequestMapping(value = "/withdraw/request/merchant/create", method = POST)
  @ResponseBody
  public void createWithdrawalRequest(
      @RequestBody final Payment payment,
      Principal principal,
      Locale locale,
      HttpServletResponse response) throws UnsupportedEncodingException {
    CreditsOperation creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName())
        .orElseThrow(InvalidAmountException::new);
    Map<String, String> result = withdrawService.createWithdrawalRequest(creditsOperation, new WithdrawData(), principal.getName(), locale);
    Cookie cookie = new Cookie("successNoty", URLEncoder.encode(result.get("success"), "UTF-8"));
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  @RequestMapping(value = "/withdraw/request/invoice/create", method = POST)
  public RedirectView submitWithdraw(WithdrawData withdrawData, Principal principal, HttpServletRequest request, Locale locale) {
    RedirectView redirectView = new RedirectView("/dashboard");
    HttpSession session = request.getSession();
    Object mutex = WebUtils.getSessionMutex(session);
    CreditsOperation creditsOperation = (CreditsOperation) session.getAttribute("creditsOperation");
    if (creditsOperation == null) {
      synchronized (mutex) {
        session.setAttribute("errorNoty", messageSource.getMessage("merchant.operationNotAvailable", null, locale));
      }
      return new RedirectView("/merchants/invoice/withdrawDetails");
    }
    Map<String, String> result = withdrawService.createWithdrawalRequest(creditsOperation, withdrawData, principal.getName(), locale);
    synchronized (mutex) {
      session.removeAttribute("creditsOperation");
      session.setAttribute("successNoty", result.get("success"));
    }
    return redirectView;
  }

  @RequestMapping(value = "/withdraw/request/revoke", method = POST)
  @ResponseBody
  public void revokeWithdrawRequest(
      @RequestParam Integer id) {
    withdrawService.revokeWithdrawalRequest(id);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/take", method = POST)
  @ResponseBody
  public void takeToWork(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.takeInWorkWithdrawalRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/return", method = POST)
  @ResponseBody
  public void returnFromWork(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.returnFromWorkWithdrawalRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/decline", method = POST)
  @ResponseBody
  public void decline(
      @RequestParam Integer id,
      @RequestParam String comment,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.declineWithdrawalRequest(id, requesterAdminId, comment);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/confirm", method = POST)
  @ResponseBody
  public void confirm(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.confirmWithdrawalRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/post", method = POST)
  @ResponseBody
  public void postHolded(
      @RequestParam Integer id,
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.postWithdrawalRequest(id, requesterAdminId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdraw/info", method = GET)
  @ResponseBody
  public WithdrawRequestsAdminTableDto getInfo(
      @RequestParam Integer id,
      Principal principal) {
    String requesterAdmin = principal.getName();
    return withdrawService.getWithdrawRequestById(id, requesterAdmin);
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
