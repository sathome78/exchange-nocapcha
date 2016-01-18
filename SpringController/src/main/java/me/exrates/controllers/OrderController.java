package me.exrates.controllers;  


import java.security.Principal;
import java.util.List;

import me.exrates.beans.Currency;
import me.exrates.services.OrderService;
import me.exrates.services.WalletService;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Controller;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.servlet.ModelAndView;  
  
@Controller  
public class OrderController {  
  
@Autowired 
OrderService orderService;  

@Autowired 
WalletService walletService;  

@RequestMapping("/orders")  
public ModelAndView myOrders(Principal principal) {  
	String email = principal.getName();

   return new ModelAndView("orders", "email", email);  
}  
 
@RequestMapping("/newordertosell")  
public ModelAndView makeNewOrderToSell() {  
    List<Currency> currList = walletService.getCurrencyList();
    Double commission = orderService.getCommissionByType("sell");
    ModelAndView model = new ModelAndView();
    model.addObject("currList", currList);
    model.addObject("commission", commission);
    model.setViewName("newordertosell");
    return model;  
}  

@RequestMapping("/createorder")  
public ModelAndView makeNewOrderToSell(Principal principal) {  
	String email = principal.getName();
    List<Currency> currList = walletService.getCurrencyList();
    Double commission = orderService.getCommissionByType("sell");
    ModelAndView model = new ModelAndView();
    model.addObject("currList", currList);
    model.addObject("commission", commission);
    model.setViewName("newordertosell");
    return model;  
}  

@RequestMapping("/newordertobuy")  
public ModelAndView makeNewOrderToBuy(Principal principal) {  
	String email = principal.getName();

   return new ModelAndView("newordertobuy", "email", email);  
}  
 
}  

