package me.exrates.controller.merchants;

import me.exrates.service.PayeerService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class PayeerMerchantController {

  @Autowired
  private PayeerService payeerService;

  private static final Logger logger = LogManager.getLogger("merchant");

  private static final String merchantInputErrorPage = "redirect:/merchants/input";

  @RequestMapping(value = "/merchants/payeer/payment/status", method = RequestMethod.POST)
  public ResponseEntity<String> statusPayment(@RequestBody Map<String, String> params, RedirectAttributes redir) throws RefillRequestAppropriateNotFoundException {
    ResponseEntity<String> response = new ResponseEntity<>(params.get("m_orderid") + "|success", OK);
    payeerService.processPayment(params);
    return response;
  }

  @RequestMapping(value = "/merchants/payeer/payment/success", method = RequestMethod.GET)
  public RedirectView successPayment(@RequestParam Map<String, String> response) {
    logger.debug(response);
    return new RedirectView("/dashboard");
  }

}
