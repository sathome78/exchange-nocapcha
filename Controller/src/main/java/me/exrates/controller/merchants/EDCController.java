package me.exrates.controller.merchants;

import me.exrates.service.EDCService;
import me.exrates.service.exception.RefillRequestNotFountException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class EDCController {


  @Autowired
  private EDCService edcService;

  private final Logger LOG = LogManager.getLogger("merchant");

  @RequestMapping(value = "/merchants/edc/payment/received", method = RequestMethod.POST)
  public ResponseEntity<Void> statusPayment(@RequestBody Map<String, String> params, RedirectAttributes redir) throws RefillRequestNotFountException {
    LOG.info("Response: " + params);
//        edcService.checkTransactionByHistory(params); TODO off for tesining
    edcService.processPayment(params);
    return new ResponseEntity<>(OK);
  }


}
