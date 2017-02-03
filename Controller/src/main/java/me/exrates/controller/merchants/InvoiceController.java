package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.InvoiceBank;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.vo.InvoiceData;
import me.exrates.service.InvoiceService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.RejectedPaymentInvoice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
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
            List<InvoiceBank> invoiceBanks = invoiceService.retrieveBanksForCurrency(creditsOperation.getCurrency().getId());
            modelAndView.addObject("invoiceBanks", invoiceBanks);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/payment/prepare",method = POST)
    public RedirectView preparePayment(InvoiceData invoiceData,
                                       final Principal principal, RedirectAttributes redirectAttributes,
                                                 HttpServletRequest request)    {
        RedirectView redirectView = new RedirectView("/dashboard");
        LOG.debug(invoiceData);

        final String email = principal.getName();
        HttpSession session = request.getSession();
        CreditsOperation creditsOperation;
        Object mutex = WebUtils.getSessionMutex(session);
        synchronized (mutex) {
            creditsOperation = (CreditsOperation) session.getAttribute("creditsOperation");
            if (creditsOperation == null) {
                redirectAttributes.addFlashAttribute("errorNoty", "No credits operation found!");
                return redirectView;
            }
        }
        try {
            invoiceData.setCreditsOperation(creditsOperation);

            final Transaction transaction = invoiceService.createPaymentInvoice(invoiceData);
            final String notification = merchantService
                    .sendDepositNotification("",
                            email, localeResolver.resolveLocale(request), creditsOperation, "merchants.depositNotificationWithCurrency" +
                                    creditsOperation.getCurrency().getName() +
                                    ".body");
            redirectAttributes.addFlashAttribute("successNoty", notification);
            synchronized (mutex) {
                session.removeAttribute("creditsOperation");
            }

        } catch (final InvalidAmountException|RejectedPaymentInvoice e) {
            final String error = messageSource.getMessage("merchants.incorrectPaymentDetails", null, localeResolver.resolveLocale(request));
            LOG.warn(error);
            redirectAttributes.addFlashAttribute("errorNoty", error);
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
