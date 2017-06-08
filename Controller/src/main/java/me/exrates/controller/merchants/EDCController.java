package me.exrates.controller.merchants;

import me.exrates.service.EDCService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class EDCController {


  @Autowired
  private EDCService edcService;

  private final Logger LOG = LogManager.getLogger("merchant");

  @RequestMapping(value = "/merchants/edc/payment/received", method = RequestMethod.POST)
  public ResponseEntity<Void> statusPayment(@RequestBody Map<String, String> params, RedirectAttributes redir) throws RefillRequestAppropriateNotFoundException {

    final ResponseEntity<Void> responseOK = new ResponseEntity<>(OK);
    LOG.info("Response: " + params);
    try {
      edcService.processPayment(params);
      return responseOK;
    }catch (RefillRequestAlreadyAcceptedException e){
      return responseOK;
    }catch (Exception e){
      return new ResponseEntity<>(BAD_REQUEST);
    }
  }


}
