package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CreditsOperation;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.UserService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.OrderDeletingException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * created by ValkSam
 */
@Controller
@Log4j2
public class WithdrawRequestController {

  @Autowired
  private MessageSource messageSource;

  @Autowired
  WithdrawService withdrawService;

  @Autowired
  UserService userService;

  @RequestMapping(value = "/withdraw/request/submit", method = POST)
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
    Map<String, String> result = withdrawService.withdrawRequest(creditsOperation, withdrawData, principal.getName(), locale);
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
      Principal principal) {
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    withdrawService.declineWithdrawalRequest(id, requesterAdminId);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(InvoiceNotFoundException.class)
  @ResponseBody
  public ErrorInfo NotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler({
      InvoiceActionIsProhibitedForCurrencyPermissionOperationException.class,
      InvoiceActionIsProhibitedForNotHolderException.class
  })
  @ResponseBody
  public ErrorInfo ForbiddenExceptionHandler(HttpServletRequest req, Exception exception) {
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
    log.error(exception);
    exception.printStackTrace();
    return new ErrorInfo(req.getRequestURL(), exception);
  }

}
