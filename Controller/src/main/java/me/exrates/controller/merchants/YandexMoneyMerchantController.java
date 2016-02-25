package me.exrates.controller.merchants;

import com.yandex.money.api.exceptions.InsufficientScopeException;
import com.yandex.money.api.exceptions.InvalidRequestException;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Session;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/yandexmoney")
@SessionAttributes(types = CreditsOperation.class, value = "creditsOperation")
public class YandexMoneyMerchantController {

    @Autowired
    private YandexMoneyService yandexMoneyService;

    @Autowired
    private MerchantService merchantService;

    private static final Logger logger = LogManager.getLogger(YandexMoneyMerchantController.class);

    private static final String merchantInputErrorPage = "redirect:/merchants/input";

    private static final String merchantOutputErrorPage = "redirect:/merchants/output";

    @RequestMapping(value = "/token/authorization", method = RequestMethod.GET)
    public RedirectView yandexMoneyTemporaryAuthorizationCodeRequest() {
        return new RedirectView(yandexMoneyService.getTemporaryAuthCode());
    }

    @RequestMapping(value = "/token/access")
    public ModelAndView yandexMoneyAccessTokenRequest(@RequestParam(value = "code") String code,RedirectAttributes redir) {
        final Optional<String> accessToken = yandexMoneyService.getAccessToken(code);
        if (!accessToken.isPresent()) {
            redir.addFlashAttribute("error", "merchants.authRejected");
            return new ModelAndView(merchantInputErrorPage);
        }
        redir.addFlashAttribute("token", accessToken.get());
        return new ModelAndView("redirect:/merchants/yandexmoney/payment/process");
    }

    //// TODO: HANDLE 500 if OperationType is not be converted
    @RequestMapping(value = "/payment/prepare", method = RequestMethod.POST)
    public ModelAndView preparePayment(@Valid @ModelAttribute("payment") Payment payment, Model data,
                                       BindingResult result, Principal principal,RedirectAttributes redir) {
        final String errorRedirectView = payment.getOperationType() == OperationType.INPUT ?
                merchantInputErrorPage : merchantOutputErrorPage;
        final Map<String, Object> model = result.getModel();
        if (result.hasErrors()) {
            return new ModelAndView(errorRedirectView, model);
        }
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            redir.addFlashAttribute("error", "merchants.invalidSum");
            return new ModelAndView(errorRedirectView);
        }
        final ModelAndView modelAndView = new ModelAndView("redirect:/merchants/yandexmoney/token/authorization");
        data.addAttribute("creditsOperation", creditsOperation.get());
        return modelAndView;
    }

    @RequestMapping(value = "/payment/process")
    public RedirectView processPayment(Principal principal, @ModelAttribute("creditsOperation") CreditsOperation creditsOperation,
                                       @ModelAttribute(value = "token") String token, RedirectAttributes redir, SessionStatus sessionStatus) {
        final Optional<RequestPayment> requestPayment = yandexMoneyService.requestInputPayment(token, creditsOperation);
        final RedirectView successView = new RedirectView("/mywallets");
        if (!requestPayment.isPresent()) {
            redir.addFlashAttribute("message","yo");
            return successView;
        }
        final RequestPayment request = requestPayment.get();
        final RedirectView failureView = new RedirectView("/merchants/".concat(
                creditsOperation.getOperationType() == OperationType.INPUT ? "input" : "output"));
        if (request.status.equals(BaseRequestPayment.Status.REFUSED)) {
            switch (request.error) {
                    case NOT_ENOUGH_FUNDS:
                        redir.addFlashAttribute("error", "merchants.notEnoughMoney");
                        return failureView;
                    case AUTHORIZATION_REJECT:
                        redir.addFlashAttribute("error", "merchants.authRejected");
                        return failureView;
                    case LIMIT_EXCEEDED:
                        redir.addFlashAttribute("error", "merchants.limitExceed");
                        return failureView;
                    case ACCOUNT_BLOCKED:
                        return new RedirectView(request.accountUnblockUri);
                    case EXT_ACTION_REQUIRED:
                        return new RedirectView(request.extActionUri);
                    default:
                        logger.fatal(request.error);
                        redir.addFlashAttribute("error", "merchants.internalError");
                        return failureView;
                }
        }
        redir.addFlashAttribute("error", "merchants.internalError");
        return failureView;
    }
}