package me.exrates.controllers.validators;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.exrates.beans.User;
import me.exrates.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class FormValidation implements Validator{

	@Autowired
	UserService userService;  
	
		
	 private Pattern pattern;  
	 private Matcher matcher;  
	  
	 private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"  
	   + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";  
	 String ID_PATTERN = "[0-9]+";  
	 String STRING_PATTERN = "[a-zA-Z]+";  
	 String MOBILE_PATTERN = "[0-9]{10}";  
	 
	 @Autowired
	 MessageSource messageSource;

	public boolean supports(Class<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void validate(Object target, Errors errors) {
		 User user = (User) target;
		 String nicknameRequired = messageSource.getMessage("validation.nicknamerequired", null, Locale.ROOT);
		 String nicknameExceed = messageSource.getMessage("validation.nicknameexceed", null, Locale.ROOT);
		 String nicknameExists = messageSource.getMessage("validation.nicknameexists", null, Locale.ROOT);
		 String emailRequired = messageSource.getMessage("validation.emailrequired", null, Locale.ROOT);
		 String emailExists = messageSource.getMessage("validation.emailexists", null, Locale.ROOT);
		 String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, Locale.ROOT);
		 String passwordRequired = messageSource.getMessage("validation.passwordrequired", null, Locale.ROOT);
		 String passwordMismatch = messageSource.getMessage("validation.passwordmismatch", null, Locale.ROOT);
		 String notReadRules = messageSource.getMessage("validation.notreadrules", null, Locale.ROOT);
		 ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nickname", "required.nickname",  
				 nicknameRequired);  
		 if (user.getNickname().length() > 40) {  
				    errors.rejectValue("nickname", "nickname.exceed", nicknameExceed);  
		}  
		if(!userService.ifNicknameIsUnique(user.getNickname())) {
				  errors.rejectValue("nickname", "nickname.incorrect", nicknameExists);
		}
		  
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",  
		    "required.email", emailRequired);  

        if (!(user.getEmail() != null && user.getEmail().isEmpty())) {  
				  pattern = Pattern.compile(EMAIL_PATTERN);  
				  matcher = pattern.matcher(user.getEmail());  
				  if (!matcher.matches()) {  
					  errors.rejectValue("email", "email.incorrect", emailIncorrect);  
				  }  
		}  
		if(!userService.ifEmailIsUnique(user.getEmail())) {
				  errors.rejectValue("email", "email.exists", emailExists);
		}
//		  ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone",  
//		    "required.phone", "Phone is required.");  
//		  
//		// phone number validation  
//		  if (!(student.getPhone() != null && student.getPhone().isEmpty())) {  
//		   pattern = Pattern.compile(MOBILE_PATTERN);  
//		   matcher = pattern.matcher(student.getPhone());  
//		   if (!matcher.matches()) {  
//		    errors.rejectValue("phone", "phone.incorrect",  
//		      "Enter a correct phone number");  
//		   }  
//		  }  
//		  
		  ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",  
		    "required.password", passwordRequired);  

		  if (!user.getPassword().equals(user.getConfirmPassword())) {  
		   errors.rejectValue("confirmPassword", "password.mismatch",  
		    passwordMismatch);  
		  }  
		  
		  if (!user.isReadRules()) {  
		   errors.rejectValue("readRules", "noselect.readRules",  
		     notReadRules);  
		  }  
		 }  
}  