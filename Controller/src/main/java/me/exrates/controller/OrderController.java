package me.exrates.controller;  


import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import me.exrates.model.Currency;
import me.exrates.model.Order;
<<<<<<< HEAD
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
=======
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
import me.exrates.service.CommissionService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;  
import org.springframework.ui.ModelMap;
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

<<<<<<< HEAD
private static final Locale ru = new Locale("ru");

@RequestMapping("/orders")  
public ModelAndView myOrders() {  
	Map<String, List<Order>> orderMap = orderService.getAllOrders();

   return new ModelAndView("orders", "orderMap", orderMap);  
}  
 
@RequestMapping(value = "/order/sell/new", method = RequestMethod.GET)  
public ModelAndView showNewOrderToSellForm(ModelAndView model) {  
	getCurrenciesAndCommission(model, OperationType.SELL);
=======
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
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
    Order order = new Order();
    model.setViewName("newordertosell");
    model.addObject(order);
    return model;  
}  


<<<<<<< HEAD
@RequestMapping(value = "/order/sell/submit", method = RequestMethod.POST)  
public ModelAndView submitNewOrderToSell(@Valid @ModelAttribute Order order, BindingResult result, ModelAndView model, Principal principal) {  
	getCurrenciesAndCommission(model,OperationType.SELL);
=======
@RequestMapping(value = "/submitordertosell", method = RequestMethod.POST)  
public ModelAndView submitNewOrderToSell(@Valid @ModelAttribute Order order, BindingResult result, ModelAndView model, Principal principal) {  
	getCurrenciesAndCommission(model, OPERATION_TYPE_SELL);
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
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

<<<<<<< HEAD

@RequestMapping(value="/order/sell/create",method = RequestMethod.POST)  
public ModelAndView recordOrderToDB(ModelAndView model, @ModelAttribute Order order, Principal principal) {  
	int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()),order.getCurrencySell());
	order.setWalletIdSell(walletIdFrom);
	order.setOperationType(OperationType.SELL);
	order.setStatus(OrderStatus.INPROCESS);
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
	if(orderService.setStatus(id, OrderStatus.CANCELLED)) {
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
=======
@RequestMapping(value="/createorder",method = RequestMethod.POST)  
public ModelAndView recordOrderToDB( ModelAndView modelAndView, @ModelAttribute Order order, Principal principal) {  
	int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()),order.getCurrencySell());
	order.setWalletIdSell(walletIdFrom);
	order.setOperationType(OPERATION_TYPE_SELL);
	if(orderService.createOrder(order)) {
		modelAndView.setViewName("ordercreated");
	}
	else {
		modelAndView.setViewName("DBError");
	}
    modelAndView.addObject(order);
    return modelAndView;  
}  

@RequestMapping(value = "/editorder", method = RequestMethod.POST)  
public ModelAndView showEditOrderToSellForm(@ModelAttribute Order order){
   ModelAndView model = new ModelAndView("editorder","order",order);  
   getCurrenciesAndCommission(model, OPERATION_TYPE_SELL);
   return model;
}  

@RequestMapping("/editorderDB")  
public ModelAndView editUser(@RequestParam String id,  
  @ModelAttribute Order order) {  
 
 //r = userService.getUser(id);  
 
 System.out.println("orderid = "+id);
 
 return new ModelAndView("editorder", "order", order);  
 
}  


@RequestMapping("/deleteorder")  
public String deleteOrder(@RequestParam int id, RedirectAttributes redirectAttributes) {  
 String msg = null;
	if(orderService.deleteOrder(id)) {
	msg = "delete";
 }
 else {
	msg = "failed";
 }
 redirectAttributes.addFlashAttribute("msg", msg);
 return "redirect:/myorders";  
}  


@RequestMapping("/newordertobuy")  
public ModelAndView makeNewOrderToBuy(Principal principal) {  
	String email = principal.getName();
   return new ModelAndView("newordertobuy", "email", email);  
}  
 
@RequestMapping("/myorders")  
public ModelAndView showMyOrders(Principal principal, ModelAndView model) {  
	String email = principal.getName();
	List<Order> orderList = orderService.getMyOrders(email);
	model.addObject("orderList", orderList);
return model;  
}  

private void getCurrenciesAndCommission(ModelAndView model, int operationType) {
	List<Currency> currList = walletService.getCurrencyList();
    double commission = commissionService.getCommissionByType(operationType);
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
    model.addObject("currList", currList);
    model.addObject("commission", commission);
}

<<<<<<< HEAD


=======
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
}  

