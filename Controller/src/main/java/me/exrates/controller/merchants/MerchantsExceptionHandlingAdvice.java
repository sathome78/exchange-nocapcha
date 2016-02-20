package me.exrates.controller.merchants;

import me.exrates.service.exception.MerchantInternalException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@ControllerAdvice
public class MerchantsExceptionHandlingAdvice {

    @ExceptionHandler(MerchantInternalException.class)
    public ModelAndView handleInternal(MerchantInternalException e) {
        return null;
    }
}