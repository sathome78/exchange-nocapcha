package me.exrates.controller.merchants;

import me.exrates.service.exception.MerchantInternalException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@ControllerAdvice
public class MerchantsExceptionHandlingAdvice {

    private String telephone1;
    private String telephone2;
    private String telephone3;
    private String telephone4;



    @ExceptionHandler(MerchantInternalException.class)
    public RedirectView handleInternal(MerchantInternalException e, HttpServletRequest request,HttpServletResponse response) throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("abc"));

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

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("abc.txt"));
        String lines = bufferedReader.readLine();

    }
}