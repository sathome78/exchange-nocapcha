package me.exrates.controller;

import me.exrates.model.CompanyWallet;
import me.exrates.model.Wallet;
import me.exrates.model.dto.UserWalletSummaryDto;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

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
        List<UserWalletSummaryDto> usersWalletsSummaryList = walletService.getUsersWalletsSummary();
        return new ModelAndView("UsersWallets", "usersWalletsSummaryList", usersWalletsSummaryList);
    }
}
