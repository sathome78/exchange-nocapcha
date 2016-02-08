package me.exrates.controller.merchants;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.Payment;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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

    @RequestMapping(value = "/merchants/input", method = RequestMethod.GET)
    public ModelAndView merchants() {
        final ModelAndView modelAndView = new ModelAndView("merchantsInputCredits");
        final List<Currency> currencies = currencyService.getAllCurrencies();
        final List<Merchant> merchants = merchantService.findAllByCurrency(currencies.get(0));
        System.out.println("asdasd");
        logger.error("ADASasdas");
        modelAndView.addObject("currencies", currencies);
        modelAndView.addObject("merchants", merchants);
        modelAndView.addObject("payment", new Payment());
        return modelAndView;
    }

    @RequestMapping(value = "/merchants/{merchant}/error", method = RequestMethod.GET)
    public ModelAndView handleErrorFromMerchant(@PathVariable String merchant, @ModelAttribute("error") String error) {
        return new ModelAndView("merchanterror").addObject("error", error);

    }
}