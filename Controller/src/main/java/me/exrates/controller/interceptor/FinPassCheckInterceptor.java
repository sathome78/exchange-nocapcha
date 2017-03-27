package me.exrates.controller.interceptor;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.EntryController;
import me.exrates.controller.annotation.FinPassCheck;
import me.exrates.controller.annotation.OnlineMethod;
import me.exrates.model.User;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by maks on 27.03.2017.
 */
@Log4j2
@Component
public class FinPassCheckInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;
    @Autowired
    private LocaleResolver localeResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        if (handler instanceof HandlerMethod) {
            try {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                if (handlerMethod.hasMethodAnnotation(FinPassCheck.class)) {
                    Map<String, Object> annotationAttributes =
                            AnnotationUtils.getAnnotationAttributes(handlerMethod.getMethodAnnotation(FinPassCheck.class));
                    boolean onlyCheckAttribute = (boolean)annotationAttributes.get("notCheckPassIfCheckOnlyParamTrue");
                    if (onlyCheckAttribute) {
                        boolean onlyCheckParam = Boolean.valueOf(request.getParameter("checkOnly"));
                        if (onlyCheckParam) {
                            return true;
                        }
                    }
                    String financePassFieldName = String.valueOf(annotationAttributes.get("paramName"));
                    String finPass = String.valueOf(request.getParameter(financePassFieldName));
                    User storedUser = userService.getUserById(userService.getIdByEmail(request.getUserPrincipal().getName()));
                    userService.checkFinPassword(finPass, storedUser, localeResolver.resolveLocale(request));
                }
            } catch (Exception e) {
                log.debug(e);
                throw new RuntimeException("processing error");
            }
        }
        return true;
    }
}
