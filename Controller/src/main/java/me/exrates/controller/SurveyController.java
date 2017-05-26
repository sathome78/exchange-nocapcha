package me.exrates.controller;

import me.exrates.controller.exception.*;
import me.exrates.service.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class SurveyController {

  private static final Logger log_result = LogManager.getLogger("survey_result");
  private static final Logger log = LogManager.getLogger("survey");
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private UserService userService;

  @RequestMapping(value = "/survey/saveAsDone", method = POST)
  @ResponseBody
  public void saveAsDone(
      @RequestParam String surveyToken,
      @RequestBody String surveyResultJsonString,
      Principal principal) {
    log_result.info(String.format("survey: %s user: %s  answer: %s", surveyToken, principal.getName(),surveyResultJsonString));
    userService.savePollAsDoneByUser(principal.getName());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
    log.error(exception);
    exception.printStackTrace();
    return new ErrorInfo(req.getRequestURL(), exception);
  }


}