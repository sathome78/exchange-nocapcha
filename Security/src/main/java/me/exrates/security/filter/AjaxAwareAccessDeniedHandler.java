package me.exrates.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OLEG on 08.12.2016.
 */
public class AjaxAwareAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private MessageSource messageSource;

    private String errorPage;

    public AjaxAwareAccessDeniedHandler(String errorPage) {
        this.errorPage = errorPage;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (!response.isCommitted()) {
            String requestedWith = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWith)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                Map<String, Object> errorInfo = new HashMap<String, Object>() {{
                    put("cause", accessDeniedException.getClass().getSimpleName());
                    put("detail", accessDeniedException.getLocalizedMessage());
                }};
                String responseString = new ObjectMapper().writeValueAsString(errorInfo);
                ServletOutputStream out = response.getOutputStream();
                out.print(responseString);
                out.flush();
            }
            else {
                request.setAttribute(WebAttributes.ACCESS_DENIED_403,
                        accessDeniedException);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage);
                dispatcher.forward(request, response);
            }
        }
    }
}
