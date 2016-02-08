package me.exrates.controller;  


import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import me.exrates.model.Currency;
import me.exrates.model.Order;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;  
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
  
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

private static final Locale ru = new Locale("ru");

@RequestMapping(value = "/orders")  
public ModelAndView myOrders() {  
	ModelAndView model = new ModelAndView();
	Map<String, List<Order>> orderMap = orderService.getAllOrders();
	model.setViewName("orders");
	model.addObject("orderMap", orderMap);
   return model;
}  

@RequestMapping(value = "/orders/sell/accept")  
public ModelAndView acceptOrder(@RequestParam int id, ModelAndView model, Principal principal, RedirectAttributes redirectAttributes){
	int userId = userService.getIdByEmail(principal.getName());
	Order order = orderService.getOrderById(id);
	int userWalletIdForBuy = walletService.getWalletId(userId, order.getCurrencyBuy());
	if(userWalletIdForBuy != 0) {
		if(walletService.ifEnoughMoney(userWalletIdForBuy, order.getAmountBuy())) {
			if(orderService.acceptOrder(userId, id)) {
				model.setViewName("acceptordersuccess");
				model.addObject("order", order);
			}
			else {
				model.setViewName("DBError");
			}
		}
		else {
			redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("validation.orderNotEnoughMoney", null, ru));
			model.setViewName("redirect:/orders");
		}
	}
	else {
		redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("validation.orderNotEnoughMoney", null, ru));
		model.setViewName("redirect:/orders");
	}
  return model;
}  


@RequestMapping(value = "/order/sell/new", method = RequestMethod.GET)  
public ModelAndView showNewOrderToSellForm(ModelAndView model) {  
	getCurrenciesAndCommission(model, OperationType.SELL);
    Order order = new Order();
    model.setViewName("newordertosell");
    model.addObject(order);
    return model;  
}  

@RequestMapping(value = "/order/sell/submit", method = RequestMethod.POST)  
public ModelAndView submitNewOrderToSell(@Valid @ModelAttribute Order order, BindingResult result, ModelAndView model, Principal principal) {  
	getCurrenciesAndCommission(model,OperationType.SELL);
	if(result.hasErrors()) {
     	model.setViewName("newordertosell");
    }
    else {
    	int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()),order.getCurrencySell());
    	boolean ifEnoughMoney = walletService.ifEnoughMoney(walletIdFrom, order.getAmountSell());
    	if(ifEnoughMoney) {
    		model.setViewName("submitorder");
    	}
    	else {
    		model.addObject("notEnoughMoney", messageSource.getMessage("validation.orderNotEnoughMoney", null, ru));
    		model.setViewName("newordertosell");
    	}
    }
	model.addObject("order",order);
	return model;  
}  

@RequestMapping(value="/order/sell/create",method = RequestMethod.POST)  
public ModelAndView recordOrderToDB(ModelAndView model, @ModelAttribute Order order, Principal principal) {  
	int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()),order.getCurrencySell());
	order.setWalletIdSell(walletIdFrom);
	order.setOperationType(OperationType.SELL);
	if((orderService.createOrder(order)) > 0) {
		model.setViewName("ordercreated");
	}
	else {
		model.setViewName("DBError");
	}
    model.addObject(order);
    return model;  
}  

@RequestMapping(value = "/order/sell/edit", method = RequestMethod.POST)  
public ModelAndView showEditOrderToSellForm(ModelAndView model, @ModelAttribute Order order){
   model = new ModelAndView("editorder","order",order);  
   getCurrenciesAndCommission(model, OperationType.SELL);
   return model;
}  


@RequestMapping("/newordertobuy")  
public ModelAndView makeNewOrderToBuy(Principal principal) {  
	String email = principal.getName();
   return new ModelAndView("newordertobuy", "email", email);  
}  

@RequestMapping("/myorders")  
public ModelAndView showMyOrders(Principal principal, ModelAndView model) {  
	String email = principal.getName();
	Map<String, List<Order>> orderMap = orderService.getMyOrders(email);
	model.addObject("orderMap", orderMap);
return model;  
}  

@RequestMapping("/myorders/edit")  
public ModelAndView editUser(@RequestParam int id, ModelAndView model) {  
 getCurrenciesAndCommission(model, OperationType.SELL);
 Order order = orderService.getOrderById(id);
 model.addObject("order", order);
 model.setViewName("editorderinprocess");
 return model;  
 
}  

@RequestMapping(value = "/myorders/submit", method = RequestMethod.POST)  
public ModelAndView submitEditedOrderToSell(@Valid @ModelAttribute Order order, BindingResult result, ModelAndView model, Principal principal) {  
	getCurrenciesAndCommission(model, OperationType.SELL);
	if(result.hasErrors()) {
     	model.setViewName("editorderinrocess");
    }
    else {
    	int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()),order.getCurrencySell());
    	boolean ifEnoughMoney = walletService.ifEnoughMoney(walletIdFrom, order.getAmountSell());
    	if(ifEnoughMoney) {
    		model.setViewName("submitorderDB");
    	}
    	else {
    		model.addObject("notEnoughMoney", messageSource.getMessage("validation.orderNotEnoughMoney", null, ru));
    		model.setViewName("editorderinprocess");
    	}
    }
	model.addObject("order",order);
	return model;  
}  

@RequestMapping(value="/myorders/update",method = RequestMethod.POST)  
public String updateOrder(@ModelAttribute Order order, Principal principal, RedirectAttributes redirectAttributes) {  
	String msg = null;
	int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()),order.getCurrencySell());
	order.setWalletIdSell(walletIdFrom);
	order.setOperationType(OperationType.SELL);
	order.setStatus(OrderStatus.OPENED);
	if(orderService.updateOrder(order)) {
		msg = "edit";
	}
	else {
		msg="editfailed";
	}
	redirectAttributes.addFlashAttribute("msg", msg);
	return "redirect:/myorders"; 
}  

@RequestMapping("/myorders/delete")  
public String deleteOrder(@RequestParam int id, RedirectAttributes redirectAttributes) {  
	String msg = null;
	if(orderService.cancellOrder(id)) {
		msg = "delete";
	}
	else {
		msg = "deletefailed";
	}
	redirectAttributes.addFlashAttribute("msg", msg);
	return "redirect:/myorders";  
}  


@RequestMapping("DBError")  
public String DBerror() {  
	return "DBError";
}  

private void getCurrenciesAndCommission(ModelAndView model, OperationType type) {
	List<Currency> currList = walletService.getCurrencyList();
    double commission = commissionService.getCommissionByType(type);
    model.addObject("currList", currList);
    model.addObject("commission", commission);
}

}  

