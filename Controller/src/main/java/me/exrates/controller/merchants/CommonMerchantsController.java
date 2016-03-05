package me.exrates.controller.merchants;

import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/")
public class CommonMerchantsController {

    private static final Logger logger = LogManager.getLogger(CommonMerchantsController.class);

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping("/test/{id}")
    public @ResponseBody List<Transaction> test(@PathVariable String id) {
        System.out.println(id);

        return transactionService.findAllByUserWallets(Arrays.asList(1,2,5));
    }

    @RequestMapping(value = "/input", method = RequestMethod.GET)
    public ModelAndView inputCredits() {
        final ModelAndView modelAndView = new ModelAndView("merchantsInputCredits");
        modelAndView.addObject("currencies",currencyService.getAllCurrencies());
        Payment payment = new Payment();
        payment.setSum(1.00);
        payment.setOperationType(OperationType.INPUT);
        modelAndView.addObject("payment", payment);
        return modelAndView;
    }

    @RequestMapping(value = "/output", method = RequestMethod.GET)
    public ModelAndView outputCredits(Principal principal) {
        final ModelAndView modelAndView = new ModelAndView("merchantsOutputCredits");
        final List<Wallet> allWallets = walletService.getAllWallets(userService.getIdByEmail(principal.getName()));
        modelAndView.addObject("wallets",allWallets);
        Payment payment = new Payment();
        payment.setSum(1.00);
        payment.setOperationType(OperationType.OUTPUT);
        modelAndView.addObject("payment", payment);
        return modelAndView;
    }

    //// TODO: HANDLE 500 if OperationType is not be converted
    @RequestMapping(value = "/{merchant}/payment/prepare", method = RequestMethod.POST)
    public ModelAndView preparePayment(@Valid @ModelAttribute("payment") Payment payment,
                                       BindingResult result, Principal principal, RedirectAttributes redir,
                                       @PathVariable String merchant, HttpSession httpSession) {
        System.out.println("HERE");
        System.out.println(payment);
        final String errorRedirectView = payment.getOperationType() == OperationType.INPUT ?
                "merchantsInputCredits": "merchantsOutputCredits";
        final Map<String, Object> model = result.getModel();
        if (result.hasErrors()) {
            return new ModelAndView(errorRedirectView, model);
        }
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            redir.addFlashAttribute("error", "merchants.invalidSum");
            return new ModelAndView(errorRedirectView);
        }
        final OperationType operationType = creditsOperation.get().getOperationType();
        String viewName;
        switch (merchant) {
            case "yandexmoney" :
                viewName =  chooseYandexMoneyViewName(operationType);
                break;
            default:
                viewName = operationType == OperationType.INPUT ? "/input" : "/output";
                redir.addAttribute("error","merchants.invalidMerchant");
                break;
        }
        final ModelAndView modelAndView = new ModelAndView("redirect:/merchants/"+viewName);
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        synchronized (mutex) {
            httpSession.setAttribute("creditsOperation",creditsOperation.get());
        }
        return modelAndView;
    }

    private String chooseYandexMoneyViewName(OperationType operationType) {
        return operationType == OperationType.INPUT ? "/yandexmoney/token/authorization"
                : "/yandexmoney/payment/process";
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public @ResponseBody Map<Integer, List<Merchant>> getMerchantsData() {
        return merchantService
                .mapMerchantsToCurrency(currencyService.getAllCurrencies());
    }

    @RequestMapping(value = "/commission/{type}",method = RequestMethod.GET)
    public @ResponseBody BigDecimal  getCommissions(@PathVariable("type") String type) {
        switch (type) {
            case "input" :
                return commissionService.findCommissionByType(OperationType.INPUT).getValue();
            case "output" :
                return commissionService.findCommissionByType(OperationType.OUTPUT).getValue();
            default:
                return null;
        }
    }

    @RequestMapping(value = "/{merchant}/error", method = RequestMethod.GET)
    public ModelAndView handleErrorFromMerchant(@PathVariable String merchant, @ModelAttribute("error") String error) {
        return new ModelAndView("merchanterror").addObject("error", error);
    }
}