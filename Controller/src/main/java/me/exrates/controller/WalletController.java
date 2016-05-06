package me.exrates.controller;

import me.exrates.model.CompanyWallet;
import me.exrates.model.Wallet;
import me.exrates.model.dto.UsersWalletsSummaryDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WalletController {

    @Autowired
    UserService userService;

    @Autowired
    WalletService walletService;

    @Autowired
    CompanyWalletService companyWalletService;

    @RequestMapping("/mywallets")
    public ModelAndView viewMyWallets(Principal principal) {
        String email = principal.getName();
        int userId = userService.getIdByEmail(email);
        List<Wallet> walletList = walletService.getAllWallets(userId);
        return new ModelAndView("mywallets", "walletList", walletList);
    }

    @RequestMapping("/companywallet")
    public ModelAndView showCompanyWalletForTesting() {
        List<CompanyWallet> companyWalletList = companyWalletService.getCompanyWallets();
        return new ModelAndView("CompanyWallets", "companyWalletList", companyWalletList);
    }

    @RequestMapping("/userswallets")
    public ModelAndView showUsersWalletsSummary() {
        List<UsersWalletsSummaryDto> usersWalletsSummaryList = walletService.getUsersWalletsSummary();
        return new ModelAndView("UsersWallets", "usersWalletsSummaryList", usersWalletsSummaryList);
    }

    @RequestMapping(value = "admin/uploadUsersWalletsSummary", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getUsersWalletsSummeryTxt() {
        return walletService.getUsersWalletsList()
                .stream()
                .map(e -> e.toString())
                .collect(Collectors.joining());
    }
}
