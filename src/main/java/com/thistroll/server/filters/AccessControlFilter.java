package com.thistroll.server.filters;

import org.springframework.beans.factory.annotation.Required;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Filter to allow cross-domain requests
 */
public class AccessControlFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        filterChain.doFilter(servletRequest, httpServletResponse);
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
