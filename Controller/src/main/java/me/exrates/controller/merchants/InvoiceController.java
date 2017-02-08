package me.exrates.controller.merchants;

import me.exrates.model.*;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.InvoiceData;
import me.exrates.service.InvoiceService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.RejectedPaymentInvoice;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("/merchants/invoice")
public class InvoiceController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    private static final Logger LOG = LogManager.getLogger("merchant");



    @RequestMapping(value = "/preSubmit", method = POST)
    public RedirectView preSubmit(final Payment payment, final Principal principal,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) {
        LOG.debug(payment);
        RedirectView redirectView = new RedirectView("/merchants/invoice/details");

        if (!merchantService.checkInputRequestsLimit(payment.getMerchant(), principal.getName())){
            redirectAttributes.addFlashAttribute("error", "merchants.InputRequestsLimit");
            return redirectView;
        }
        if (/*payment.getCurrency() == 10 || */payment.getCurrency() == 12 || payment.getCurrency() == 13){
            redirectAttributes.addFlashAttribute("error", "merchants.withoutInvoiceWallet");
            return redirectView;
        }
        Optional<CreditsOperation> creditsOperationPrepared = merchantService
                .prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperationPrepared.isPresent()) {
            redirectAttributes.addFlashAttribute("error","merchants.incorrectPaymentDetails");
        } else {
            CreditsOperation creditsOperation = creditsOperationPrepared.get();
            LOG.debug(creditsOperation);
            HttpSession session = request.getSession();
            Object mutex = WebUtils.getSessionMutex(session);
            synchronized (mutex) {
                session.setAttribute("creditsOperation", creditsOperation);
            }
        }
        return redirectView;
    }

    @RequestMapping(value = "/details", method = GET)
    public ModelAndView invoiceDetails(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("/globalPages/invoiceDeatils");
        Map<String, ?> flashAttrMap = RequestContextUtils.getInputFlashMap(request);
        LOG.debug(flashAttrMap);
        if (flashAttrMap != null && flashAttrMap.containsKey("error")) {
            return modelAndView;
        }
        HttpSession session = request.getSession();
        Object mutex = WebUtils.getSessionMutex(session);
        CreditsOperation creditsOperation;

        synchronized (mutex) {
            creditsOperation = (CreditsOperation) session.getAttribute("creditsOperation");
        }
        if (creditsOperation == null) {
            modelAndView.addObject("error", "merchant.operationNotAvailable");
        } else {
            modelAndView.addObject("creditsOperation", creditsOperation);
            List<InvoiceBank> invoiceBanks = invoiceService.findBanksForCurrency(creditsOperation.getCurrency().getId());
            String notSelected = messageSource.getMessage("merchants.notSelected", null, localeResolver.resolveLocale(request));
            invoiceBanks.add(0, new InvoiceBank(-1, creditsOperation.getCurrency().getId(), notSelected, notSelected, notSelected));
            modelAndView.addObject("invoiceBanks", invoiceBanks);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/payment/prepare",method = POST)
    public RedirectView preparePayment(InvoiceData invoiceData,
                                       final Principal principal, RedirectAttributes redirectAttributes,
                                                 HttpServletRequest request)    {
        LOG.debug(invoiceData);
        final String email = principal.getName();
        HttpSession session = request.getSession();
        CreditsOperation creditsOperation;
        Object mutex = WebUtils.getSessionMutex(session);
        synchronized (mutex) {
            creditsOperation = (CreditsOperation) session.getAttribute("creditsOperation");
            if (creditsOperation == null) {
                redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("merchant.operationNotAvailable", null,
                        localeResolver.resolveLocale(request)));
                return new RedirectView("/dashboard");
            }
        }
        try {
            invoiceData.setCreditsOperation(creditsOperation);

            final Transaction transaction = invoiceService.createPaymentInvoice(invoiceData);
            InvoiceBank invoiceBank = invoiceService.findBankById(invoiceData.getBankId());
            String toWallet = invoiceBank.getName() + ": " + invoiceBank.getAccountNumber();
            final String notification = merchantService
                    .sendDepositNotification(toWallet,
                            email, localeResolver.resolveLocale(request), creditsOperation, "merchants.depositNotificationWithCurrency" +
                                    creditsOperation.getCurrency().getName() +
                                    ".body");
            synchronized (mutex) {
                session.setAttribute("successNoty", notification);
                session.removeAttribute("creditsOperation");
            }
            return new RedirectView("/dashboard?startupPage=myhistory&startupSubPage=myinputoutput");

        } catch (final InvalidAmountException|RejectedPaymentInvoice e) {
            final String error = messageSource.getMessage("merchants.incorrectPaymentDetails", null, localeResolver.resolveLocale(request));
            LOG.warn(error);
            synchronized (mutex) {
                session.setAttribute("errorNoty", error);
            }
            return new RedirectView("/dashboard");
        }
    }


    @RequestMapping(value = "/payment/cancel",method = POST)
    public RedirectView cancelPayment(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object mutex = WebUtils.getSessionMutex(session);
        synchronized (mutex) {
            session.removeAttribute("creditsOperation");
        }
        return new RedirectView("/dashboard");
    }

    @RequestMapping(value = "/payment/confirmation", method = GET)
    public ModelAndView confirmationPage(@RequestParam Integer transactionId) {
        LOG.debug(transactionId);
        ModelAndView modelAndView = new ModelAndView("/globalPages/invoiceConfirm");
        Optional<InvoiceRequest> invoiceRequestResult = invoiceService.findRequestById(transactionId);
        if (!invoiceRequestResult.isPresent()) {
            modelAndView.addObject("error", "merchants.error.invoiceRequestNotFound");
        } else {
            InvoiceRequest invoiceRequest = invoiceRequestResult.get();
            modelAndView.addObject("invoiceRequest", invoiceRequest);
            List<String> bankNames = invoiceService.findBanksForCurrency(invoiceRequest.getTransaction().getCurrency().getId())
                    .stream().map(InvoiceBank::getName).collect(Collectors.toList());
            modelAndView.addObject("bankNames", bankNames);
            if (bankNames.stream().noneMatch(name -> name.equals(invoiceRequest.getPayeeBankName()))) {
                modelAndView.addObject("otherBank", invoiceRequest.getPayeeBankName());
            }
        }
        return modelAndView;
    }
    @RequestMapping(value = "/payment/confirm", method = POST)
    public RedirectView confirmInvoice(InvoiceConfirmData invoiceConfirmData, HttpServletRequest request) {
        LOG.debug(invoiceConfirmData);
        RedirectView redirectView = new RedirectView("/dashboard?startupPage=myhistory&startupSubPage=myinputoutput");
        Optional<InvoiceRequest> invoiceRequestResult = invoiceService.findUnconfirmedRequestById(invoiceConfirmData.getInvoiceId());
        HttpSession session = request.getSession();
        Object mutex = WebUtils.getSessionMutex(session);
        if (!invoiceRequestResult.isPresent()) {
            synchronized (mutex) {
                session.setAttribute("errorNoty", messageSource.getMessage("merchants.error.invoiceRequestNotFound", null,
                        localeResolver.resolveLocale(request)));
            }
        } else {
            InvoiceRequest invoiceRequest = invoiceRequestResult.get();
            invoiceRequest.setPayeeBankName(invoiceConfirmData.getPayeeBankName());
            invoiceRequest.setPayeeAccount(invoiceConfirmData.getUserAccount());
            invoiceRequest.setUserFullName(invoiceConfirmData.getUserFullName());
            //html escaping to prevent XSS
            invoiceRequest.setRemark(StringEscapeUtils.escapeHtml(invoiceConfirmData.getRemark()));
            invoiceService.updateConfirmationInfo(invoiceRequest);
            synchronized (mutex) {
                session.setAttribute("successNoty", messageSource.getMessage("merchants.invoiceConfirm.noty", null,
                        localeResolver.resolveLocale(request)));
            }
        }
        return redirectView;
    }



    @RequestMapping(value = "/payment/accept",method = GET)
    public RedirectView acceptPayment(@RequestParam int id, RedirectAttributes redir, Principal principal){

        if (!invoiceService.provideTransaction(id, principal.getName())){
            final String message = "merchants.internalError";
            redir.addFlashAttribute("message", message);
        }

        return new RedirectView("/2a8fy7b07dxe44/invoiceConfirmation");
    }

}
