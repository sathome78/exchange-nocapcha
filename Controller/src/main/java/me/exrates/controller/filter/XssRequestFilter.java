package me.exrates.controller.filter;


import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;


public class XssRequestFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            parameterMap.forEach((k,v) -> {
                Stream.of(v).forEach(va -> {
                    if (!va.equals(Jsoup.clean(va, Whitelist.basic()))) {
                        throw new RuntimeException("xss detected!");
                    }
                });
            });
            chain.doFilter(request, response);
        } catch (RuntimeException e) {
            ((HttpServletResponse)response).sendRedirect("/dashboard");
        }

    }

    @Override
    public void destroy() {
    }

}
