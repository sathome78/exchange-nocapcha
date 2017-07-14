package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.annotation.FinPassCheck;
import me.exrates.controller.exception.CheckFinPassException;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.InvalidNicknameException;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.MyWalletConfirmationDetailDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import me.exrates.service.exception.AbsentFinPasswordException;
import me.exrates.service.exception.NotConfirmedFinPasswordException;
import me.exrates.service.exception.WrongFinPasswordException;
import me.exrates.service.exception.invoice.MerchantException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
public class WalletController {


    @Autowired
    UserService userService;

    @Autowired
    WalletService walletService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CompanyWalletService companyWalletService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    UserRoleService userRoleService;

    @RequestMapping("/companywallet")
    public ModelAndView showCompanyWalletForTesting(Principal principal) {
        Integer requesterUserId = userService.getIdByEmail(principal.getName());
        List<CompanyWallet> companyWalletList = companyWalletService.getCompanyWalletsSummaryForPermittedCurrencyList(requesterUserId);
        return new ModelAndView("CompanyWallets", "companyWalletList", companyWalletList);
    }

    @RequestMapping("/dashboard/myWalletsConfirmationDetail")
    @ResponseBody
    public List<MyWalletConfirmationDetailDto> showWalletsConfirmationDetail(
            @RequestParam Integer walletId,
            HttpServletRequest request) {
        return walletService.getWalletConfirmationDetail(walletId, localeResolver.resolveLocale(request));
    }
    @RequestMapping("/transfer")
    public ModelAndView transfer(@RequestParam String currencyName, Principal principal) {
        ModelAndView modelAndView = new ModelAndView("globalPages/transfer");
        Currency currency = currencyService.findByName(currencyName);
        User user = userService.findByEmail(principal.getName());
        Wallet wallet = walletService.findByUserAndCurrency(user, currency);
        BigDecimal maxForTransfer = resolveMaxTransferAmount(wallet, currencyName);
        BigDecimal minAmount = currencyService.retrieveMinLimitForRoleAndCurrency(user.getRole(), OperationType.USER_TRANSFER, currency.getId());
        modelAndView.addObject("currency", currency);
        modelAndView.addObject("wallet", wallet);
        modelAndView.addObject("balance", BigDecimalProcessing.formatNonePoint(wallet.getActiveBalance(), false));
        modelAndView.addObject("maxForTransfer", maxForTransfer);
        modelAndView.addObject("minAmount", minAmount);
        return modelAndView;
    }

    private BigDecimal resolveMaxTransferAmount(Wallet wallet, String currencyName) {
        BigDecimal commissionRate = commissionService.findCommissionByTypeAndRole(OperationType.USER_TRANSFER, userService.getUserRoleFromSecurityContext()).getValue();
        BigDecimal commissionDecimal = BigDecimalProcessing.doAction(commissionRate, BigDecimal.valueOf(100), ActionType.DEVIDE);
        BigDecimal commissionMultiplier = BigDecimalProcessing.doAction(commissionDecimal, BigDecimal.ONE, ActionType.ADD);
        BigDecimal maxForTransfer = BigDecimalProcessing.doAction(wallet.getActiveBalance(), commissionMultiplier, ActionType.DEVIDE)
                .setScale(currencyService.resolvePrecision(currencyName), BigDecimal.ROUND_DOWN);
        return maxForTransfer;

    }

    /**@param checkOnly - used to verify payment, without fin pass, but not perform transfer
     * */
    @FinPassCheck(notCheckPassIfCheckOnlyParamTrue = true)
    @RequestMapping(value = "/transfer/submit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> submitTransfer(@RequestParam Integer walletId,
                                                              @RequestParam String nickname,
                                                              @RequestParam BigDecimal amount,
                                                              @RequestParam(value = "checkOnly", defaultValue = "false")
                                                                          boolean checkOnly,
                                                              Principal principal,
                                                              HttpServletRequest request) {
        if (!nickname.matches("^\\D+[\\w\\d\\-_]+")) {
            throw new InvalidNicknameException(messageSource.getMessage("transfer.invalidNickname", null, localeResolver.resolveLocale(request)));
        }
        String principalNickname = userService.findByEmail(principal.getName()).getNickname();
        if (nickname.equals(principalNickname)) {
            throw new InvalidNicknameException(messageSource.getMessage("transfer.selfNickname", null, localeResolver.resolveLocale(request)));
        }
        String result = walletService.transferCostsToUser(walletId, nickname, amount, localeResolver.resolveLocale(request), checkOnly);
        return new ResponseEntity<>(Collections.singletonMap("result", result), HttpStatus.OK);
    }
    
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({AbsentFinPasswordException.class, NotConfirmedFinPasswordException.class, WrongFinPasswordException.class, CheckFinPassException.class})
    @ResponseBody
    public ErrorInfo finPassWxceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage(((MerchantException)(exception)).getReason(), null,  localeResolver.resolveLocale(req)));
    }
    
    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ErrorInfo commonErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
