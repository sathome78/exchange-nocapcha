package me.exrates.controller.merchants;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Wallet;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/")
public class CommonMerchantsController {

    private static final Logger logger = LogManager.getLogger("merchant");

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
    private PendingPaymentDao pendingPaymentDao;

    private final MathContext mathContext = new MathContext(9, RoundingMode.CEILING);

    @RequestMapping(value = "/input", method = RequestMethod.GET)
    public ModelAndView inputCredits() {
        final ModelAndView modelAndView = new ModelAndView("merchantsInputCredits");
        modelAndView.addObject("currencies",currencyService.getAllCurrencies());
        Payment payment = new Payment();
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
        payment.setOperationType(OperationType.OUTPUT);
        modelAndView.addObject("payment", payment);
        return modelAndView;
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public @ResponseBody List<MerchantCurrency> getMerchantsData() {
        final List<Integer> currenciesId = currencyService
                .getAllCurrencies()
                .stream()
                .mapToInt(Currency::getId)
                .boxed()
                .collect(Collectors.toList());
        return merchantService
                .findAllByCurrencies(currenciesId);
    }

    @RequestMapping(value = "/commission/{type}",method = RequestMethod.GET)
    public @ResponseBody
    BigDecimal getCommissions(@PathVariable("type") String type) {
        switch (type) {
            case "input" :
                return commissionService
                    .findCommissionByType(OperationType.INPUT)
                    .getValue()
                    .divide(BigDecimal.valueOf(100L),mathContext)
                    .setScale(9, BigDecimal.ROUND_CEILING);
            case "output" :
                return commissionService
                    .findCommissionByType(OperationType.OUTPUT)
                    .getValue()
                    .divide(BigDecimal.valueOf(100L),mathContext)
                    .setScale(9, BigDecimal.ROUND_CEILING);
            default:
                return null;
        }
    }
}