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

    private List<String> origins;

    public static final String HEADER_NAME = "origin";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String origin = httpServletRequest.getHeader(HEADER_NAME);
        if (origin != null && origins.contains(origin.toLowerCase())) {
            httpServletResponse.addHeader("Access-Control-Allow-Origin", origin);
        }
        filterChain.doFilter(servletRequest, httpServletResponse);
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Required
    public void setOrigins(List<String> origins) {
        this.origins = origins;
    }
}
