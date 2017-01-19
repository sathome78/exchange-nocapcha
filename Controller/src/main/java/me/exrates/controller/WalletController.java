package me.exrates.controller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.MyWalletConfirmationDetailDto;
import me.exrates.model.dto.UserWalletSummaryDto;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class WalletController {

    private static final Logger LOG = LogManager.getLogger(WalletController.class);


    @Autowired
    UserService userService;

    @Autowired
    WalletService walletService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CompanyWalletService companyWalletService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @RequestMapping("/companywallet")
    public ModelAndView showCompanyWalletForTesting() {
        List<CompanyWallet> companyWalletList = companyWalletService.getCompanyWallets();
        return new ModelAndView("CompanyWallets", "companyWalletList", companyWalletList);
    }

    @RequestMapping("/userswallets")
    public ModelAndView showUsersWalletsSummary() {
        List<UserWalletSummaryDto> usersWalletsSummaryList = walletService.getUsersWalletsSummary();
        return new ModelAndView("UsersWallets", "usersWalletsSummaryList", usersWalletsSummaryList);
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
        modelAndView.addObject("currency", currency);
        modelAndView.addObject("wallet", wallet);
        return modelAndView;
    }

    @RequestMapping(value = "/transfer/submit", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> submitTransfer(@RequestParam Integer walletId,
                                               @RequestParam String nickname,
                                               @RequestParam Integer currencyId,
                                               @RequestParam BigDecimal amount,
                                               HttpServletRequest request) {
        String result = walletService.transferCostsToUser(walletId, nickname, currencyId, amount, localeResolver.resolveLocale(request));
        LOG.debug(result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ErrorInfo commonErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
