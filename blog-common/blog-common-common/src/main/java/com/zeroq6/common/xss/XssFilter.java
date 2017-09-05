package com.zeroq6.common.xss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class XssFilter implements Filter {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String ignoreUriPrefix;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ignoreUriPrefix = filterConfig.getInitParameter("ignoreUriPrefix");

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        if (!httpServletRequest.getRequestURI().startsWith(httpServletRequest.getContextPath() + ignoreUriPrefix)) {
            chain.doFilter(new XssHttpServletRequestWrapper(httpServletRequest), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
