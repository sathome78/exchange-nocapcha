package me.exrates.controller;  


import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import me.exrates.model.Currency;
import me.exrates.model.Order;
import me.exrates.service.CommissionService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;  
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;  
  
@Controller  
public class OrderController {  
  
@Autowired 
OrderService orderService;  

@Autowired 
CommissionService commissionService;  

@Autowired 
WalletService walletService;  

@Autowired 
UserService userService;  

@Autowired
MessageSource messageSource;

private static final int OPERATION_TYPE_SELL = 3;
private static final int OPERATION_TYPE_BUY = 4;
private static final Locale ru = new Locale("ru");

@RequestMapping("/orders")  
public ModelAndView myOrders(Principal principal) {  
	String email = principal.getName();

   return new ModelAndView("orders", "email", email);  
}  
 
@RequestMapping(value = "/newordertosell", method = RequestMethod.GET)  
public ModelAndView showNewOrderToSellForm(ModelAndView model) {  
	getCurrenciesAndCommission(model, OPERATION_TYPE_SELL);
    Order order = new Order();
    model.setViewName("newordertosell");
    model.addObject(order);
    return model;  
}  


@RequestMapping(value = "/submitordertosell", method = RequestMethod.POST)  
public ModelAndView submitNewOrderToSell(@Valid @ModelAttribute Order order, BindingResult result, ModelAndView model, Principal principal) {  
	getCurrenciesAndCommission(model, OPERATION_TYPE_SELL);
	if(result.hasErrors()) {
     	model.setViewName("newordertosell");
    }
    else {
    	int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()),order.getCurrencySell());
    	boolean ifEnoughMoney = walletService.ifEnoughMoney(walletIdFrom, order.getAmountSell(), OPERATION_TYPE_SELL);
    	if(ifEnoughMoney) {
    		model.setViewName("submitorder");
    	}
    	else {
    		model.addObject("notEnoughMoney", messageSource.getMessage("validation.orderNotEnoughMoney", null, ru));
    		model.setViewName("newordertosell");
    	}
    }
	getCurrenciesAndCommission(model, OPERATION_TYPE_SELL);
	model.addObject("order",order);
	return model;  
}  

@RequestMapping("/createorder")  
public ModelAndView recordOrderToDB(@ModelAttribute Order order, Principal principal) {  
	System.out.println("order = "+order.toString());
	System.out.println(order.getAmountSell());
	System.out.println(order.getCurrencySell());
	int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()),order.getCurrencySell());
	order.setWalletIdSell(walletIdFrom);
	order.setOperationType(OPERATION_TYPE_SELL);
	
	ModelAndView modelAndView = new ModelAndView();
	if(orderService.createOrder(order)) {
		modelAndView.setViewName("ordercreated");
	}
	else {
		modelAndView.setViewName("DBError");
	}
    modelAndView.addObject(order);
    return modelAndView;  
}  

@RequestMapping("/newordertobuy")  
public ModelAndView makeNewOrderToBuy(Principal principal) {  
	String email = principal.getName();

   return new ModelAndView("newordertobuy", "email", email);  
}  
 
private void getCurrenciesAndCommission(ModelAndView model, int operationType) {
	List<Currency> currList = walletService.getCurrencyList();
    double commission = commissionService.getCommissionByType(operationType);
    model.addObject("currList", currList);
    model.addObject("commission", commission);
}

}  

