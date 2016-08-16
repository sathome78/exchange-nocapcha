package me.exrates.controller.filter;

import me.exrates.controller.OnlineRestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by ogolv on 16.08.2016.
 */
public class EncodingFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger(OnlineRestController.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        LOGGER.debug(request.getCharacterEncoding());
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
