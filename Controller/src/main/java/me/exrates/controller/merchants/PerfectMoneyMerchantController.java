package me.exrates.controller.merchants;

import me.exrates.model.Payment;
import me.exrates.service.PerfectMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/perfectmoney")
public class PerfectMoneyMerchantController {

    @Autowired
    private PerfectMoneyService perfectMoneyService;

    @RequestMapping(value = "payment/prepare",method = RequestMethod.POST,
            headers = {"Content-type=application/json"})
    public @ResponseBody  Map<String,String> preparePayment(@RequestBody Payment payment, Principal principal,HttpServletRequest httpServletRequest) {
        final String header = httpServletRequest.getHeader("X-FORWARDED-FOR");
        System.out.println(header);
        return perfectMoneyService.preparePayment(payment,principal).get();
    }

    @RequestMapping(value = "payment/success")
    public void successPayment() {System.out.println("here");}

    @RequestMapping(value = "payment/status")
    public void payStatus() {
        System.out.println("here");
    }

    @RequestMapping(value = "payment/failure")
    public void failurePayment() {
        System.out.println("here");
    }
}