package me.exrates.controller.merchants;

import me.exrates.model.Wallet;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class CommonMerchantsController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/merchants", method = RequestMethod.GET)
    public ModelAndView getPage(Principal principal) {
        String email = principal.getName();
        int idByEmail = userService.getIdByEmail(email);
        List<Wallet> userWallets = walletService.getAllWallets(idByEmail);
        ModelAndView modelAndView = new ModelAndView("merchants");
        modelAndView.addObject("userWallets",userWallets);
        return modelAndView;
    }

    @RequestMapping(value = "/merchants/{merchant}/error",method = RequestMethod.GET)
    public ModelAndView handleErrorFromMerchant(@PathVariable String merchant, @RequestParam("error") String error) {;
        return new ModelAndView("merchanterror").addObject("error",error);
    }
}