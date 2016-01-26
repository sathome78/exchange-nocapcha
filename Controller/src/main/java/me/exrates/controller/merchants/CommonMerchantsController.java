package me.exrates.controller.merchants;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class CommonMerchantsController {
    @RequestMapping(value = "/merchants", method = RequestMethod.GET)
    public String getPage() {
        return "merchants";
    }

    @RequestMapping(value = "/merchants/{merchant}/error",method = RequestMethod.GET)
    public ModelAndView handleErrorFromMerchant(@PathVariable String merchant, @RequestParam("error") String error) {;
        return new ModelAndView("merchanterror").addObject("error",error);
    }
}