package me.exrates.controller.merchants;

import com.google.gson.Gson;
import javafx.util.Pair;
import me.exrates.dao.CompanyWalletDao;
import me.exrates.model.Commission;
import me.exrates.model.Merchant;
import me.exrates.model.Payment;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    private CompanyWalletDao companyWalletDao;

    @Autowired
    private CommissionService commissionService;

    private static final Gson gson = new Gson();

    @RequestMapping(value = "/merchants/input", method = RequestMethod.GET)
    public ModelAndView merchants() {
        final ModelAndView modelAndView = new ModelAndView("merchantsInputCredits");
        modelAndView.addObject("currencies",currencyService.getAllCurrencies());
        modelAndView.addObject("payment", new Payment());
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
                return commissionService.getCommissionByType(Commission.OperationType.INPUT.type);
            case "output" :
                return commissionService.getCommissionByType(Commission.OperationType.OUTPUT.type);
            default:
                return null;
        }
    }

    @RequestMapping(value = "/merchants/{merchant}/error", method = RequestMethod.GET)
    public ModelAndView handleErrorFromMerchant(@PathVariable String merchant, @ModelAttribute("error") String error) {
        return new ModelAndView("merchanterror").addObject("error", error);
    }
}