package me.exrates.controller.merchants;

import me.exrates.model.*;
import me.exrates.model.enums.ActionType;
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

import static me.exrates.model.enums.OperationType.*;
import static me.exrates.model.enums.UserCommentTopicEnum.*;
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
  private CommissionService commissionService;

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
      refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(merchantCurrencyData, principal.getName());
      modelAndView.addObject("merchantCurrencyData", merchantCurrencyData);
      List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), REFILL_CURRENCY_WARNING);
      modelAndView.addObject("warningCodeList", warningCodeList);
      return modelAndView;
    } catch (Exception e) {
      ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
      modelAndView.addObject("errorNoty", e.getClass().getSimpleName() + ": " + e.getMessage());
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
      List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, OperationType.OUTPUT);
      withdrawService.retrieveAddressAndAdditionalParamsForWithdrawForMerchantCurrencies(merchantCurrencyData);
      modelAndView.addObject("merchantCurrencyData", merchantCurrencyData);
      List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), WITHDRAW_CURRENCY_WARNING);
      modelAndView.addObject("warningCodeList", warningCodeList);
      return modelAndView;
    } catch (Exception e) {
      ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
      modelAndView.addObject("errorNoty", e.getClass().getSimpleName() + ": " + e.getMessage());
      return modelAndView;
    }
  }

  @RequestMapping(value = "/transfer", method = GET)
  public ModelAndView transfer(
      @RequestParam("currency") String currencyName,
      Principal principal) {
    try {
      ModelAndView modelAndView = new ModelAndView("globalPages/transfer");
      Currency currency = currencyService.findByName(currencyName);
      modelAndView.addObject("currency", currency);
      Wallet wallet = walletService.findByUserAndCurrency(userService.findByEmail(principal.getName()), currency);
      modelAndView.addObject("wallet", wallet);
      modelAndView.addObject("balance", BigDecimalProcessing.formatNonePoint(wallet.getActiveBalance(), false));
      Payment payment = new Payment();
      payment.setOperationType(USER_TRANSFER);
      modelAndView.addObject("payment", payment);
      BigDecimal minTransferSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), USER_TRANSFER, currency.getId());
      modelAndView.addObject("minTransferSum", minTransferSum);
      BigDecimal maxTransferSum = resolveMaxTransferAmount(wallet, currencyName);
      modelAndView.addObject("maxTransferSum", maxTransferSum);
      Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForWithdraw();
      modelAndView.addObject("scaleForCurrency", scaleForCurrency);
      List<String> initialWarningCodeList = currencyService.getWarningForCurrency(currency.getId(), INITIAL_TRANSFER_CURRENCY_WARNING);
      modelAndView.addObject("initialWarningCodeList", initialWarningCodeList);
      List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), TRANSFER_CURRENCY_WARNING);
      modelAndView.addObject("warningCodeList", warningCodeList);
      return modelAndView;
    } catch (Exception e) {
      ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
      modelAndView.addObject("errorNoty", e.getClass().getSimpleName() + ": " + e.getMessage());
      return modelAndView;
    }
  }

  @RequestMapping("/_transfer")
  public ModelAndView _transfer(@RequestParam String currencyName, Principal principal) {
    ModelAndView modelAndView = new ModelAndView("globalPages/transfer");
    Currency currency = currencyService.findByName(currencyName);
    User user = userService.findByEmail(principal.getName());
    Wallet wallet = walletService.findByUserAndCurrency(user, currency);
    BigDecimal maxForTransfer = resolveMaxTransferAmount(wallet, currencyName);
    BigDecimal minAmount = currencyService.retrieveMinLimitForRoleAndCurrency(user.getRole(), USER_TRANSFER, currency.getId());
    modelAndView.addObject("currency", currency);
    modelAndView.addObject("wallet", wallet);
    modelAndView.addObject("balance", BigDecimalProcessing.formatNonePoint(wallet.getActiveBalance(), false));
    modelAndView.addObject("maxForTransfer", maxForTransfer);
    modelAndView.addObject("minAmount", minAmount);
    return modelAndView;
  }

  private BigDecimal resolveMaxTransferAmount(Wallet wallet, String currencyName) {
    BigDecimal commissionRate = commissionService.findCommissionByTypeAndRole(USER_TRANSFER, userService.getUserRoleFromSecurityContext()).getValue();
    BigDecimal commissionDecimal = BigDecimalProcessing.doAction(commissionRate, BigDecimal.valueOf(100), ActionType.DEVIDE);
    BigDecimal commissionMultiplier = BigDecimalProcessing.doAction(commissionDecimal, BigDecimal.ONE, ActionType.ADD);
    BigDecimal maxForTransfer = BigDecimalProcessing.doAction(wallet.getActiveBalance(), commissionMultiplier, ActionType.DEVIDE)
        .setScale(currencyService.resolvePrecision(currencyName), BigDecimal.ROUND_DOWN);
    return maxForTransfer;

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
