package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.CheckFinPassException;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CompanyWallet;
import me.exrates.model.dto.MyWalletConfirmationDetailDto;
import me.exrates.service.*;
import me.exrates.service.exception.AbsentFinPasswordException;
import me.exrates.service.exception.NotConfirmedFinPasswordException;
import me.exrates.service.exception.WrongFinPasswordException;
import me.exrates.service.exception.invoice.MerchantException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

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

    /**@param checkOnly - used to verify payment, without fin pass, but not perform transfer
     * *//*
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
    }*/
    
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
