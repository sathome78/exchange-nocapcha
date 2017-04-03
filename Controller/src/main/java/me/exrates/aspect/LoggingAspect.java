package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 31.03.2017.
 */
@Aspect
@Log4j2
@Component
public class LoggingAspect {

  
  
  @AfterThrowing(pointcut = "execution(public * *(..))", throwing = "ex")
  public void logException(JoinPoint joinPoint, Exception ex) {
    
    log.debug(String.format("error in method %s with args: %s", joinPoint.getSignature().getName(),
            String.join("", Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.toList()))) );
    
    log.debug(String.format("exception: %s : %s \n stacktrace: %s", ex.getClass().getSimpleName(), ex.getMessage(), ExceptionUtils.getStackTrace(ex)));
  
  }

}
