package me.exrates.controller.merchants;

import me.exrates.model.Payment;
import me.exrates.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class CommonMerchantsController {

    @Autowired
    private CurrencyService currencyService;

    @RequestMapping(value = "/merchants", method = RequestMethod.GET)
    public ModelAndView getPage() {
        ModelAndView modelAndView = new ModelAndView("merchants");
        modelAndView.addObject("currencyList",currencyService.getAllCurrencies());
        modelAndView.addObject("payment",new Payment());
        return modelAndView;
    }

    @RequestMapping(value = "/merchants/{merchant}/error",method = RequestMethod.GET)
    public ModelAndView handleErrorFromMerchant(@PathVariable String merchant, @ModelAttribute("error") String error) {
        return new ModelAndView("merchanterror").addObject("error",error);
    }
}