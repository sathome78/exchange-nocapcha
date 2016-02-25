package me.exrates.controller.merchants;

import me.exrates.service.exception.MerchantInternalException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@ControllerAdvice
public class MerchantsExceptionHandlingAdvice {

    @ExceptionHandler(MerchantInternalException.class)
    public RedirectView handleInternal(MerchantInternalException e, HttpServletRequest request,HttpServletResponse response) {
        final String view = "/merchants/".concat(
                e.getMessage()
                        .endsWith("Input") ? "input" : "output"
        );
        final RedirectView redirectView = new RedirectView(view);
        final FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("error","merchants.internalError");
        }
        return redirectView;
    }
}