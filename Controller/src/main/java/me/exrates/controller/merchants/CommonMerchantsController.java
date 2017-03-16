package me.exrates.controller.merchants;

import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Wallet;
import me.exrates.model.enums.CurrencyWarningType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;
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
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource source;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "/input", method = GET)
    public ModelAndView inputCredits(@RequestParam("currency") String currency) {

        final ModelAndView modelAndView = new ModelAndView("globalPages/merchantsInput");
        int currencyId = currencyService.findByName(currency).getId();

        modelAndView.addObject("currency",currencyId);
        modelAndView.addObject("currencyName",currency);
        Payment payment = new Payment();
        payment.setOperationType(INPUT);
        modelAndView.addObject("payment", payment);

        final List<Integer> currenciesId = Collections.singletonList(currencyId);
        modelAndView.addObject("merchantCurrencyData",merchantService.findAllByCurrencies(currenciesId, OperationType.INPUT));
        modelAndView.addObject("minAmount", currencyService.retrieveMinLimitForRoleAndCurrency(userService.getCurrentUserRole(), INPUT, currencyId));
        
        //TODO refactor for a single method call
        Optional<String> warningCodeSingleAddress = currencyService.getWarningForCurrency(currencyId, CurrencyWarningType.SINGLE_ADDRESS);
        warningCodeSingleAddress.ifPresent(s -> modelAndView.addObject("warningSingleAddress", s));
        Optional<String> warningCodeTimeout = currencyService.getWarningForCurrency(currencyId, CurrencyWarningType.TIMEOUT);
        warningCodeTimeout.ifPresent(s -> modelAndView.addObject("warningCodeTimeout", s));
        return modelAndView;
    }

    @RequestMapping(value = "/output", method = GET)
    public ModelAndView outputCredits(@RequestParam("currency") String currencyName, Principal principal) {
        final ModelAndView modelAndView = new ModelAndView("globalPages/merchantsOutput");
        final List<Wallet> wallets = walletService.getAllWallets(userService.getIdByEmail(principal.getName()));
        final Currency currency = currencyService.findByName(currencyName);
        final Wallet wallet = walletService.findByUserAndCurrency(userService.findByEmail(principal.getName()), currency);
        final Payment payment = new Payment();
        payment.setOperationType(OUTPUT);
        final BigDecimal minWithdrawSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getCurrentUserRole(), OUTPUT, currency.getId());

        modelAndView.addObject("currency",currency);

        modelAndView.addObject("wallet",wallet);
        modelAndView.addObject("payment", payment);
        modelAndView.addObject("minWithdrawSum", minWithdrawSum);
        final List<Integer> currenciesId = new ArrayList<>();
        currenciesId.add(currency.getId());
        modelAndView.addObject("merchantCurrencyData",merchantService.findAllByCurrencies(currenciesId, OperationType.OUTPUT));

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
                .findAllByCurrencies(currenciesId, OperationType.INPUT);
    }

    @RequestMapping(value = "/commission", method = GET)
    @ResponseBody
    public Map<String,String> getCommissions(final @RequestParam("type") OperationType type,
                                      final @RequestParam("amount") BigDecimal amount,
                                      final @RequestParam("currency") String currency,
                                      final @RequestParam("merchant") String merchant)
    {
        return merchantService.computeCommissionAndMapAllToString(amount, type, currency, merchant);
    }

    @RequestMapping(value="/payment/withdraw", method = POST)
    public ResponseEntity<Map<String,String>> withdraw(@RequestBody final Payment payment,
                                                       final Principal principal, final Locale locale) {
        final ResponseEntity<Map<String, String>> error = new ResponseEntity<>(
                singletonMap("failure",
                        source.getMessage("merchants.withdrawRequestError", null, locale)),
                BAD_REQUEST);
        try {
            return merchantService.prepareCreditsOperation(payment, principal.getName())
                    .map(creditsOperation -> merchantService.withdrawRequest(creditsOperation, new WithdrawData(), principal.getName(), locale))
                    .map(response -> new ResponseEntity<>(response, OK))
                    .orElseGet(() -> error);
        } catch (final NotEnoughUserWalletMoneyException e) {
            return error;
        }
    }

    @RequestMapping(value = "/withdrawal/request/accept",method = POST)
    @ResponseBody
    public ResponseEntity<Map<String,String>> acceptWithdrawRequest(final @RequestParam("requestId") int request,
                                                                    final Locale locale, final Principal principal) {
        final Map<String, String> result = merchantService.acceptWithdrawalRequest(request, locale, principal);
        if (result.containsKey("error")) {
            return new ResponseEntity<>(result, BAD_REQUEST);
        }
        return new ResponseEntity<>(result, OK);
    }

    @RequestMapping(value = "/withdrawal/request/decline")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> declineWithdrawRequest(final @RequestParam("requestId") int request,
                                                                    final Locale locale, Principal principal) {
        final Map<String, Object> result = merchantService.declineWithdrawalRequest(request, locale, principal.getName());
        if (result.containsKey("error")) {
            return new ResponseEntity<>(result, BAD_REQUEST);
        }
        return new ResponseEntity<>(result, OK);
    }
}
