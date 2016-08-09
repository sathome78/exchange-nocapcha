package me.exrates.controller.validator;

import me.exrates.model.form.FeedbackMessageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ogolv on 09.08.2016.
 */
@Component
public class FeedbackMessageFormValidator implements Validator {
    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Autowired
    MessageSource messageSource;

    private Locale locale = new Locale("en");


    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    public void validate(Object target, Errors errors, Locale locale) {
        this.locale = locale;
        validate(target, errors);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FeedbackMessageForm messageForm = (FeedbackMessageForm) target;
        String senderNameRequired = messageSource.getMessage("validation.nameRequired", null, locale);
        String emailRequired = messageSource.getMessage("validation.emailrequired", null, locale);
        String emailIncorrect = messageSource.getMessage("validation.emailincorrect", null, locale);
        String messageRequired = messageSource.getMessage("validation.messageRequired", null, locale);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "senderName", "required.senderName",
                senderNameRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "senderEmail", "required.senderEmail",
                emailRequired);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "messageText", "required.messageText",
                messageRequired);
        if (!(messageForm.getSenderEmail() != null && messageForm.getSenderEmail().isEmpty())) {
            pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(messageForm.getSenderEmail());
            if (!matcher.matches()) {
                errors.rejectValue("senderEmail", "email.incorrect", emailIncorrect);
            }
        }

    }
}
