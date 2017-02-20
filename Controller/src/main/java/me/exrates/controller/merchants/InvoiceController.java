package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.*;
import me.exrates.model.enums.UserActionOnInvoiceEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.InvoiceData;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.InvoiceService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserFilesService;
import me.exrates.service.exception.FileLoadingException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.invoice.IllegalInvoiceRequestStatusException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.exception.invoice.RejectedPaymentInvoice;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("/merchants/invoice")
@Log4j
public class InvoiceController {

  @Autowired
  private MerchantService merchantService;

  @Autowired
  private InvoiceService invoiceService;

  @Autowired
   private MessageSource messageSource;

  @Autowired
  private LocaleResolver localeResolver;


  @RequestMapping(value = "/preSubmit", method = POST)
  public RedirectView preSubmit(final Payment payment, final Principal principal,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
    log.debug(payment);
    RedirectView redirectView = new RedirectView("/merchants/invoice/details");

        if (!merchantService.checkInputRequestsLimit(payment.getMerchant(), principal.getName())){
            redirectAttributes.addFlashAttribute("error", "merchants.InputRequestsLimit");
            return redirectView;
        }
        if (/*payment.getCurrency() == 10 || */payment.getCurrency() == 12 || payment.getCurrency() == 13){
            redirectAttributes.addFlashAttribute("error", "merchants.withoutInvoiceWallet");
            return redirectView;
        }
        BigDecimal addition;

        if (payment.getCurrency() == 10) {
            addition = BigDecimal.valueOf(Math.random() * 899 + 100).setScale(0, BigDecimal.ROUND_DOWN);
        } else {
            addition = BigDecimal.ZERO;
        }Optional<CreditsOperation> creditsOperationPrepared = merchantService
                .prepareCreditsOperation(payment, addition,principal.getName());
        if (!creditsOperationPrepared.isPresent()) {
            redirectAttributes.addFlashAttribute("error","merchants.incorrectPaymentDetails");
        } else {
            CreditsOperation creditsOperation = creditsOperationPrepared.get();
            log.debug(creditsOperation);
            HttpSession session = request.getSession();
            Object mutex = WebUtils.getSessionMutex(session);
            synchronized (mutex) {
                session.setAttribute("creditsOperation", creditsOperation);session.setAttribute("addition", addition);
            }
        }
        return redirectView;
    }

  @RequestMapping(value = "/details", method = GET)
  public ModelAndView invoiceDetails(HttpServletRequest request) {
    ModelAndView modelAndView = new ModelAndView("/globalPages/invoiceDetails");
    Map<String, ?> flashAttrMap = RequestContextUtils.getInputFlashMap(request);
    log.debug(flashAttrMap);
    if (flashAttrMap != null && flashAttrMap.containsKey("error")) {
      return modelAndView;
    }
    HttpSession session = request.getSession();
    Object mutex = WebUtils.getSessionMutex(session);
    CreditsOperation creditsOperation;
        BigDecimal addition;

        synchronized (mutex) {
            creditsOperation = (CreditsOperation) session.getAttribute("creditsOperation");
            addition = (BigDecimal) session.getAttribute("addition");
        }
        if (creditsOperation == null) {
            modelAndView.addObject("error", "merchant.operationNotAvailable");
        } else {
            modelAndView.addObject("creditsOperation", creditsOperation);
            if (addition.signum() > 0) {
                modelAndView.addObject("additionMessage", messageSource.getMessage("merchants.input.addition",
                        new Object[]{addition + " " + creditsOperation.getCurrency().getName()}, localeResolver.resolveLocale(request)));
            }
            List<InvoiceBank> invoiceBanks = invoiceService.findBanksForCurrency(creditsOperation.getCurrency().getId());
            String notSelected = messageSource.getMessage("merchants.notSelected", null, localeResolver.resolveLocale(request));
            invoiceBanks.add(0, new InvoiceBank(-1, creditsOperation.getCurrency().getId(), notSelected, notSelected, notSelected));
            modelAndView.addObject("invoiceBanks", invoiceBanks);
        }
        return modelAndView;
    }

  @RequestMapping(value = "/payment/prepare", method = POST)
  public RedirectView preparePayment(InvoiceData invoiceData,
                                     final Principal principal, RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) {
    log.debug(invoiceData);
    final String email = principal.getName();
    HttpSession session = request.getSession();
    CreditsOperation creditsOperation;
    Object mutex = WebUtils.getSessionMutex(session);
    synchronized (mutex) {
      creditsOperation = (CreditsOperation) session.getAttribute("creditsOperation");
      session.removeAttribute("creditsOperation");
      if (creditsOperation == null) {
        redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("merchant.operationNotAvailable", null,
            localeResolver.resolveLocale(request)));
        return new RedirectView("/dashboard");
      }
    }
    try {
      invoiceData.setCreditsOperation(creditsOperation);
      invoiceService.createPaymentInvoice(invoiceData);
      InvoiceBank invoiceBank = invoiceService.findBankById(invoiceData.getBankId());
      String toWallet = invoiceBank.getName() + ": " + invoiceBank.getAccountNumber();
      final String notification = merchantService
          .sendDepositNotification(toWallet,
              email, localeResolver.resolveLocale(request), creditsOperation, "merchants.depositNotificationWithCurrency" +
                  creditsOperation.getCurrency().getName() +
                  ".body");
      redirectAttributes.addFlashAttribute("successNoty", notification);
      return new RedirectView("/dashboard?startupPage=myhistory&startupSubPage=myinputoutput");
    } catch (final InvalidAmountException | RejectedPaymentInvoice e) {
      String error = messageSource.getMessage("merchants.incorrectPaymentDetails", null, localeResolver.resolveLocale(request));
      log.warn(error);
      redirectAttributes.addFlashAttribute("errorNoty", error);
    } catch (final Exception e) {
      String error = messageSource.getMessage("merchants.internalError", null, localeResolver.resolveLocale(request))
          .concat("</br>").concat(e.getMessage());
      log.error(ExceptionUtils.getStackTrace(e));
      redirectAttributes.addFlashAttribute("errorNoty", error);
    }
    return new RedirectView("/dashboard");
  }


  @RequestMapping(value = "/payment/cancel", method = POST)
  public RedirectView cancelPayment(HttpServletRequest request) {
    HttpSession session = request.getSession();
    Object mutex = WebUtils.getSessionMutex(session);
    synchronized (mutex) {
      session.removeAttribute("creditsOperation");
    }
    return new RedirectView("/dashboard");
  }

  @RequestMapping(value = "/payment/confirmation", method = GET)
  public ModelAndView confirmationPage(
      @RequestParam Integer transactionId,
      @RequestParam(required = false) String action) {
    ModelAndView modelAndView = new ModelAndView("/globalPages/invoiceConfirm");
    modelAndView.addObject("revoke", "revoke".equals(action));
    Optional<InvoiceRequest> invoiceRequestResult = invoiceService.findRequestById(transactionId);
    if (!invoiceRequestResult.isPresent()) {
      modelAndView.addObject("error", "merchants.error.invoiceRequestNotFound");
    } else {
      InvoiceRequest invoiceRequest = invoiceRequestResult.get();
      modelAndView.addObject("invoiceRequest", invoiceRequest);
        List<ClientBank> banks = invoiceService.findClientBanksForCurrency(invoiceRequest.getTransaction().getCurrency().getId());
        modelAndView.addObject("banks", banks);
        if (invoiceRequest.getPayerBankName() != null && banks.stream().noneMatch(bank -> invoiceRequest.getPayerBankName().equals(bank.getName()))) {
            modelAndView.addObject("otherBank", invoiceRequest.getPayerBankName());
        }
    }
    return modelAndView;
  }

  @RequestMapping(value = "/payment/confirm", method = POST)
  public RedirectView confirmInvoice(
      @RequestParam(required = false) String action,
      InvoiceConfirmData invoiceConfirmData,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    RedirectView redirectView = new RedirectView("/dashboard?startupPage=myhistory&startupSubPage=myinputoutput");
    UserActionOnInvoiceEnum userActionOnInvoiceEnum = UserActionOnInvoiceEnum.convert(action);
    try {
      invoiceService.userActionOnInvoice(invoiceConfirmData, userActionOnInvoiceEnum, localeResolver.resolveLocale(request));
    } catch (IllegalInvoiceRequestStatusException e) {
      redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("merchants.invoice.error.notAllowedOperation", null, localeResolver.resolveLocale(request)));
    } catch (InvoiceNotFoundException e) {
      redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("merchants.error.invoiceRequestNotFound", null, localeResolver.resolveLocale(request)));
    } catch (FileLoadingException e) {
        redirectAttributes.addFlashAttribute("errorNoty", e.getMessage());
    } catch (Exception e) {
      log.error(e.getMessage());
      e.printStackTrace();
      redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("merchants.internalError", null, localeResolver.resolveLocale(request)));
    }
    return redirectView;
  }

  @RequestMapping(value = "/payment/accept", method = GET)
  public RedirectView acceptPayment(@RequestParam int id, RedirectAttributes redir, Principal principal) throws Exception {
    invoiceService.acceptInvoiceAndProvideTransaction(id, id, principal.getName());
    return new RedirectView("/2a8fy7b07dxe44/invoiceConfirmation");
  }

  @RequestMapping(value = "/payment/decline", method = GET)
  public RedirectView declinePayment(@RequestParam int id, RedirectAttributes redir, Principal principal) throws Exception {
    invoiceService.declineInvoice(id, id, principal.getName());
    return new RedirectView("/2a8fy7b07dxe44/invoiceConfirmation");
  }

    @RequestMapping(value = "/withdraw/prepare", method = POST)
    public RedirectView prepareWithdraw(Payment payment, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        RedirectView redirectView = new RedirectView("/merchants/invoice/withdrawDetails");
        Optional<CreditsOperation> creditsOperationResult = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperationResult.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "merchants.incorrectPaymentDetails");
        } else {
            CreditsOperation creditsOperation = creditsOperationResult.get();
            HttpSession session = request.getSession();
            Object mutex = WebUtils.getSessionMutex(session);
            synchronized (mutex) {
                session.setAttribute("creditsOperation", creditsOperation);
            }
        }



        return redirectView;
    }


    @RequestMapping(value = "/withdrawDetails", method = GET)
    public ModelAndView withdrawDetails(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("/globalPages/withdrawInvoice");
        HttpSession session = request.getSession();
        CreditsOperation creditsOperation = (CreditsOperation) session.getAttribute("creditsOperation");
        if (creditsOperation == null) {
            modelAndView.addObject("error", "merchant.operationNotAvailable");
        } else {
            modelAndView.addObject("payment", creditsOperation);
            List<ClientBank> banks = invoiceService.findClientBanksForCurrency(creditsOperation.getCurrency().getId());
            modelAndView.addObject("banks", banks);
        }

        return modelAndView;
    }

    @RequestMapping(value = "/withdraw/submit", method = POST)
    public RedirectView submitWithdraw(WithdrawData withdrawData, Principal principal, HttpServletRequest request) {
        RedirectView redirectView = new RedirectView("/dashboard");
        HttpSession session = request.getSession();
        Object mutex = WebUtils.getSessionMutex(session);
        CreditsOperation creditsOperation = (CreditsOperation) session.getAttribute("creditsOperation");
        if (creditsOperation == null) {
            synchronized (mutex) {
                session.setAttribute("errorNoty",  messageSource.getMessage("merchant.operationNotAvailable", null,
                        localeResolver.resolveLocale(request)));
            }
            return new RedirectView("/merchants/invoice/withdrawDetails");

        }
        Map<String, String> result = merchantService.withdrawRequest(creditsOperation, withdrawData, principal.getName(),
                localeResolver.resolveLocale(request));
        synchronized (mutex) {
            session.removeAttribute("creditsOperation");
            session.setAttribute("successNoty",  result.get("success"));
        }
        return redirectView;
    }


  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));
    return new ErrorInfo(req.getRequestURL(), exception);
  }
}
