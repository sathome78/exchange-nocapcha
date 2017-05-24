package me.exrates.controller.merchants;

import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Wallet;
import me.exrates.model.enums.OperationType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.REFILL_CURRENCY_WARNING;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
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
  private MessageSource messageSource;

  @Autowired
  WithdrawService withdrawService;

  @Autowired
  RefillService refillService;

  private static final Logger LOG = LogManager.getLogger("merchant");

  @RequestMapping(value = "/merchants/input", method = GET)
  public ModelAndView inputCredits(
      @RequestParam("currency") String currencyName,
      Principal principal) {
    try {
      ModelAndView modelAndView = new ModelAndView("globalPages/merchantsInput");
      Currency currency = currencyService.findByName(currencyName);
      modelAndView.addObject("currency", currency);
      Payment payment = new Payment();
      payment.setOperationType(INPUT);
      modelAndView.addObject("payment", payment);
      BigDecimal minRefillSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), INPUT, currency.getId());
      modelAndView.addObject("minRefillSum", minRefillSum);
      Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForRefill();
      modelAndView.addObject("scaleForCurrency", scaleForCurrency);
      List<Integer> currenciesId = Collections.singletonList(currency.getId());
      List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, OperationType.INPUT);
      refillService.retrieveAddressAndAdditionalParamsForMerchantCurrencies(merchantCurrencyData, principal.getName());
      modelAndView.addObject("merchantCurrencyData", merchantCurrencyData);
      List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), REFILL_CURRENCY_WARNING);
      modelAndView.addObject("warningCodeList", warningCodeList);
      return modelAndView;
    } catch (Exception e) {
      ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
      modelAndView.addObject("errorNoty", e.getClass().getSimpleName()+": "+e.getMessage());
      return modelAndView;
    }
  }

  @RequestMapping(value = "/merchants/output", method = GET)
  public ModelAndView outputCredits(
      @RequestParam("currency") String currencyName,
      Principal principal) {
    try {
      ModelAndView modelAndView = new ModelAndView("globalPages/merchantsOutput");
      Currency currency = currencyService.findByName(currencyName);
      modelAndView.addObject("currency", currency);
      Wallet wallet = walletService.findByUserAndCurrency(userService.findByEmail(principal.getName()), currency);
      modelAndView.addObject("wallet", wallet);
      modelAndView.addObject("balance", BigDecimalProcessing.formatNonePoint(wallet.getActiveBalance(), false));
      Payment payment = new Payment();
      payment.setOperationType(OUTPUT);
      modelAndView.addObject("payment", payment);
      BigDecimal minWithdrawSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), OUTPUT, currency.getId());
      modelAndView.addObject("minWithdrawSum", minWithdrawSum);
      Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForWithdraw();
      modelAndView.addObject("scaleForCurrency", scaleForCurrency);
      List<Integer> currenciesId = Collections.singletonList(currency.getId());
      modelAndView.addObject("merchantCurrencyData", merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, OperationType.OUTPUT));
      List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), WITHDRAW_CURRENCY_WARNING);
      modelAndView.addObject("warningCodeList", warningCodeList);
      return modelAndView;
    } catch (Exception e) {
      ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
      modelAndView.addObject("errorNoty", e.getClass().getSimpleName()+": "+e.getMessage());
      return modelAndView;
    }
  }

  @RequestMapping(value = "/merchants/data", method = GET)
  public
  @ResponseBody
  List<MerchantCurrency> getMerchantsData() {
    List<Integer> currenciesId = currencyService
        .getAllCurrencies()
        .stream()
        .mapToInt(Currency::getId)
        .boxed()
        .collect(Collectors.toList());
    return merchantService
        .getAllUnblockedForOperationTypeByCurrencies(currenciesId, OperationType.INPUT);
  }


}
