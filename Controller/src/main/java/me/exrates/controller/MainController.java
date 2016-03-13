package me.exrates.controller;

import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.OperationView;
import me.exrates.model.User;
import me.exrates.security.service.UserSecureService;
import me.exrates.service.OrderService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.impl.UserServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;
  
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

private static final Logger logger = LogManager.getLogger(MainController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;
    
    @Autowired 
    private OrderService orderService;  
  
 @RequestMapping("/403")
 public String error403() {  
   return "403";  
 } 
  
 @RequestMapping("/register")  
 public ModelAndView registerUser(HttpServletRequest request) {
	 User user = new User();
	 return new ModelAndView("register", "user", user);
 }  
  
 @RequestMapping(value = "/create", method = RequestMethod.POST)
 public ModelAndView createUser(@ModelAttribute User user, BindingResult result, ModelMap model, HttpServletRequest request) {  
	 boolean flag=false;
	 registerFormValidation.validate(user, result);
	 user.setPhone("");
	 if(result.hasErrors()){
    	 return new ModelAndView("register", "user", user); 
     }
     
     else{
    	 user = (User) result.getModel().get("user");  
    	 try {
    		 userService.create(user);
    		 flag=true;
    		 logger.info("User registered with parameters = "+user.toString());   	
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		logger.error("User can't be registered with parameters = "+user.toString()+"  "+e.getMessage());
    	 }
     if(flag) return new ModelAndView("ProveRegistration", "user", user); 
     else  	return new ModelAndView("DBError", "user", user);
     }       	    
 }  
	 
  @RequestMapping(value = "/registrationConfirm")
  public ModelAndView verifyEmail(WebRequest request, @RequestParam("token") String token) {   
	  ModelAndView model = new ModelAndView();
	  try {
		  userService.verifyUserEmail(token);
 		  model.setViewName("RegistrationConfirmed");
	  } catch (Exception e) {
		  model.setViewName("DBError");
		  e.printStackTrace();
		  logger.error("Error while verifing user registration email  "+e.getLocalizedMessage());
	  }
	  return model;
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
        List<OperationView> list = transactionService.showMyOperationHistory(principal.getName());
       return new ModelAndView("transaction","transactions",list);
    }
 
}  

