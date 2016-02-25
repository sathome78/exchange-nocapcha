package me.exrates.controller.merchants;

import me.exrates.dao.TransactionDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;

import me.exrates.service.exception.MerchantInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.comparator.BooleanComparator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class CommonMerchantsController {

    private static final Logger logger = LogManager.getLogger(CommonMerchantsController.class);

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



    @RequestMapping(value = "/merchants/input", method = RequestMethod.GET)
    public ModelAndView inputCredits() {
        final ModelAndView modelAndView = new ModelAndView("merchantsInputCredits");
        modelAndView.addObject("currencies",currencyService.getAllCurrencies());
        Payment payment = new Payment();
        payment.setSum(1.00);
        payment.setOperationType(OperationType.INPUT);
        modelAndView.addObject("payment", payment);
        return modelAndView;
    }

    @RequestMapping(value = "/merchants/output", method = RequestMethod.GET)
    public ModelAndView outputCredits(Principal principal) {
        final ModelAndView modelAndView = new ModelAndView("merchantsOutputCredits");
        final List<Wallet> allWallets = walletService.getAllWallets(userService.getIdByEmail(principal.getName()));
        modelAndView.addObject("wallets",allWallets);
        CreditsWithdrawal creditsWithdrawal = new CreditsWithdrawal();
        creditsWithdrawal.setSum(1.00);
        modelAndView.addObject("payment", creditsWithdrawal);
        return modelAndView;
    }

    @RequestMapping(value = "/merchants/data", method = RequestMethod.GET)
    public @ResponseBody Map<Integer, List<Merchant>> getMerchantsData() {
        return merchantService
                .mapMerchantsToCurrency(currencyService.getAllCurrencies());
    }

    @RequestMapping(value = "/merchants/commission/{type}",method = RequestMethod.GET)
    public @ResponseBody Double  getCommissions(@PathVariable("type") String type) {
        switch (type) {
            case "input" :
                return commissionService.getCommissionByType(OperationType.INPUT);
            case "output" :
                return commissionService.getCommissionByType(OperationType.OUTPUT);
            default:
                return null;
        }
    }

    @RequestMapping(value = "/merchants/{merchant}/error", method = RequestMethod.GET)
    public ModelAndView handleErrorFromMerchant(@PathVariable String merchant, @ModelAttribute("error") String error) {
        return new ModelAndView("merchanterror").addObject("error", error);
    }
}