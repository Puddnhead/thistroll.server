package com.thistroll.server.filters;

import org.springframework.beans.factory.annotation.Required;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to allow cross-domain requests
 */
public class AccessControlFilter implements Filter {

    private String origins;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.addHeader("Access-Control-Allow-Origin", origins);
        filterChain.doFilter(servletRequest, httpServletResponse);
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Required
    public void setOrigins(String origins) {
        this.origins = origins;
    }
}
