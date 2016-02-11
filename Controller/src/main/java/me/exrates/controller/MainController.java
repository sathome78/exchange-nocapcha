package me.exrates.controller;  

import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import me.exrates.security.service.UserSecureService;
import me.exrates.service.CurrencyService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;  
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;  
  
@Controller  
public class MainController {  
  
@Autowired 
UserService userService;  

@Autowired 
UserSecureService userSecureService;  

@Autowired 
RegisterFormValidation registerFormValidation;  

@Autowired
HttpServletRequest request;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;
  
 @RequestMapping("/admin")  
 public ModelAndView admin() {  
   
  return new ModelAndView("admin", "id", null);  
 }  
 
 @RequestMapping("/403")  
 public String error403() {  
   return "403";  
 } 
  
 @RequestMapping("/register")  
 public ModelAndView registerUser(@ModelAttribute User user) {
     System.out.println("here");
     return new ModelAndView("register", "user", user);
 }  
  
 @RequestMapping("/create")  
 public ModelAndView createUser(User user, BindingResult result, ModelMap model) {  

	 registerFormValidation.validate(user, result);  
	 if(result.hasErrors()){
    	 return new ModelAndView("register", "user", user); 
     }
     
     else{
    	 user = (User) result.getModel().get("user");  
    	 if(userService.create(user)) {
         	// logger.info("User registered with parameters = "+user.toString());
        	return new ModelAndView("ProveRegistration", "user", user); 
         }
         else{
         	//logger.error("User couldn't be registered with parameters = "+user.toString());
        	return new ModelAndView("DBError", "user", user);
         }
	
	    
 }  
 
 } 
  @RequestMapping("/personalpage")  
  public ModelAndView gotoPersonalPage(@ModelAttribute User user, Principal principal) {  
	  String host = request.getRemoteHost();
	  String email = principal.getName();
	  String userIP = userService.logIP(email, host);
   return new ModelAndView("personalpage", "userIP", userIP);  
  }
  
  @RequestMapping("/loginfailed")  
  public ModelAndView authfailed(@ModelAttribute User user) {  
     
   return new ModelAndView("loginfailed", "user", user);  
  }  
  
 
 @RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Invalid username and password!");
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		model.setViewName("login");

		return model;

	}

    @RequestMapping(value = "/transaction")
    public ModelAndView transactions(Principal principal) {
        final int idByEmail = userService.getIdByEmail(principal.getName());
        final List<Transaction> allByUserId = transactionService.findAllByUserId(idByEmail);
        allByUserId.forEach(transaction ->
                transaction.setCurrency(walletService.getCurrencyName(walletService.getCurrencyId(transaction.getWalletId()))));
        final ModelAndView modelAndView = new ModelAndView("transaction");
        return modelAndView.addObject("transactions",allByUserId);
    }
 
}  

