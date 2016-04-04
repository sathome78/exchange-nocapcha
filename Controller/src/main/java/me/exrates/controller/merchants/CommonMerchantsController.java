package me.exrates.controller.merchants;

import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/")
public class CommonMerchantsController {

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
    private MessageSource source;

    @Autowired
    private WithdrawRequestDao withdrawRequestDao;

    private static final Logger LOGGER = LogManager.getLogger("merchant");
    private final MathContext mathContext = new MathContext(9, RoundingMode.CEILING);

    @RequestMapping(value = "/test")
    public @ResponseBody List<WithdrawRequest> test() {
        return withdrawRequestDao.findAll();
    }

    @RequestMapping(value = "/input", method = GET)
    public ModelAndView inputCredits() {
        final ModelAndView modelAndView = new ModelAndView("merchantsInputCredits");
        modelAndView.addObject("currencies",currencyService.getAllCurrencies());
        Payment payment = new Payment();
        payment.setOperationType(OperationType.INPUT);
        modelAndView.addObject("payment", payment);
        return modelAndView;
    }

    @RequestMapping(value = "/output", method = GET)
    public ModelAndView outputCredits(Principal principal) {
        final ModelAndView modelAndView = new ModelAndView("merchantsOutputCredits");
        final List<Wallet> allWallets = walletService.getAllWallets(userService.getIdByEmail(principal.getName()));
        modelAndView.addObject("wallets",allWallets);
        Payment payment = new Payment();
        payment.setOperationType(OperationType.OUTPUT);
        modelAndView.addObject("payment", payment);
        return modelAndView;
    }

    @RequestMapping(value = "/data", method = GET)
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

    @RequestMapping(value = "/commission/{type}",method = GET)
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

    @RequestMapping(value="/payment/withdraw", method = POST,
            consumes = "application/json",
            produces = "application/json")
    public @ResponseBody
    ResponseEntity<Map<String,String>> withdraw(@RequestBody final Payment payment,
                                                final Principal principal, final Locale locale) {
        LOGGER.info(payment);
        final Map<String, String> response = merchantService.prepareCreditsOperation(payment, principal.getName())
                .map((creditsOperation)->merchantService.withdrawRequest(creditsOperation,locale,principal))
                .orElse(Collections
                        .singletonMap("error", source.getMessage("merchants.withdrawRequestError",null,locale)));
        return response.containsKey("error") ?
                new ResponseEntity<>(response, BAD_REQUEST) :
                new ResponseEntity<>(response, OK);
    }
}
