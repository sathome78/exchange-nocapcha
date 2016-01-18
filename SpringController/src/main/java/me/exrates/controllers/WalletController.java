package me.exrates.controllers;

import java.security.Principal;
import java.util.List;

import me.exrates.beans.Wallet;
import me.exrates.services.UserService;
import me.exrates.services.WalletService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WalletController {

	@Autowired 
	UserService userService;
	
	@Autowired 
	WalletService walletService;
	
	@RequestMapping("/mywallets")  
	 public ModelAndView viewMyWallets(Principal principal) {  
		String email = principal.getName();
		int userId = userService.getIdByEmail(email);
	    List<Wallet> walletList = walletService.getAllWallets(userId);
	    return new ModelAndView("mywallets", "walletList", walletList);  
	 }  
}
