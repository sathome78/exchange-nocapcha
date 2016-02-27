package me.exrates.controller.merchants;

import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.utils.Strings;
import me.exrates.model.CreditsOperation;
import me.exrates.model.enums.OperationType;
import me.exrates.service.MerchantService;
import me.exrates.service.YandexMoneyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/yandexmoney")
public class YandexMoneyMerchantController {

    @Autowired
    private YandexMoneyService yandexMoneyService;

    @Autowired
    private MerchantService merchantService;

    private static final Logger logger = LogManager.getLogger(YandexMoneyMerchantController.class);

    private static final String merchantInputErrorPage = "redirect:/merchants/input";

    @RequestMapping(value = "/token/authorization", method = RequestMethod.GET)
    public RedirectView yandexMoneyTemporaryAuthorizationCodeRequest() {
        return new RedirectView(yandexMoneyService.getTemporaryAuthCode());
    }

    @RequestMapping(value = "/token/access")
    public ModelAndView yandexMoneyAccessTokenRequest(@RequestParam(value = "code",   required = false) String code,RedirectAttributes redir) {
        if (Strings.isNullOrEmpty(code)) {
            redir.addFlashAttribute("error", "merchants.authRejected");
            return new ModelAndView(merchantInputErrorPage);
        }
        final Optional<String> accessToken = yandexMoneyService.getAccessToken(code);
        if (!accessToken.isPresent()) {
            redir.addFlashAttribute("error", "merchants.authRejected");
            return new ModelAndView(merchantInputErrorPage);
        }
        redir.addFlashAttribute("token", accessToken.get());
        return new ModelAndView("redirect:/merchants/yandexmoney/payment/process");
    }



    @RequestMapping(value = "/payment/process")
    public RedirectView processPayment(@ModelAttribute(value = "token") String token, RedirectAttributes redir,
                                       HttpSession httpSession) {
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        final CreditsOperation creditsOperation;
        synchronized (mutex) {
            creditsOperation = (CreditsOperation) httpSession.getAttribute("creditsOperation");
            httpSession.removeAttribute("creditsOperation");
        }
        final Optional<RequestPayment> requestPayment = yandexMoneyService.requestPayment(token,creditsOperation);
        final RedirectView successView = new RedirectView("/mywallets");
        if (!requestPayment.isPresent()) {
            final String sumCurrency = creditsOperation.getAmount().setScale(2,BigDecimal.ROUND_CEILING) + " " + creditsOperation.getCurrency().getName();
            final String message = creditsOperation.getOperationType() == OperationType.INPUT ? "merchants.successfulBalanceDeposit"
                    : "merchants.successfulBalanceWithdraw";
            redir.addFlashAttribute("message",message);
            redir.addFlashAttribute("sumCurrency",sumCurrency);
            return successView;
        }
        final RequestPayment request = requestPayment.get();
        final RedirectView failureView = new RedirectView("/merchants/".concat(
                creditsOperation.getOperationType() == OperationType.INPUT ? "input" : "output"));
        if (request.status.equals(BaseRequestPayment.Status.REFUSED)) {
            switch (request.error) {
                case PAYEE_NOT_FOUND:
                    redir.addFlashAttribute("error", "merchants.incorrectPaymentDetails");
                    return failureView;
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