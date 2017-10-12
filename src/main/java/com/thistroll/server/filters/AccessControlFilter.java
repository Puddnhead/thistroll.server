package com.thistroll.server.filters;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * Filter to allow cross-domain requests
 */
public class AccessControlFilter implements Filter {

    private Set<String> allowedOrigins;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String origin = ((HttpServletRequest)servletRequest).getHeader("Origin");

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        if (StringUtils.isNotEmpty(origin) && allowedOrigins.contains(origin)) {
            httpServletResponse.addHeader("Access-Control-Allow-Origin", origin);
        } else {
            httpServletResponse.addHeader("Access-Control-Allow-Origin", "null");
        }
        httpServletResponse.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        httpServletResponse.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(servletRequest, httpServletResponse);
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Required
    public void setAllowedOrigins(Set<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
