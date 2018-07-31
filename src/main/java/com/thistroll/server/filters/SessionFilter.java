package com.thistroll.server.filters;

import com.thistroll.domain.Session;
import com.thistroll.domain.enums.Environments;
import com.thistroll.exceptions.SessionNotFoundException;
import com.thistroll.server.RequestValues;
import com.thistroll.service.client.SessionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet Filter that loads session information from a cookie and saves it to a thread local, then writes a
 * Set-Cookie header to the response with the updated session information
 *
 * Created by MVW on 9/5/2017.
 */
public class SessionFilter implements Filter {

    private SessionService sessionService;

    private String environment;

    @Override
    public void init(FilterConfig filterConfig) {
         // do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            setSessionInRequestValues(servletRequest, servletResponse);
            setSessionCookieInResponseIfNotLoggingInOrOut(servletRequest);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            RequestValues.setSession(null);
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }

    private void setSessionInRequestValues(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        RequestValues.setHttpServletResponse(httpServletResponse);

        String sessionId = getSessionCookie(httpServletRequest);
        if (StringUtils.isNotEmpty(sessionId)) {
            try {
                Session session = sessionService.getSession(sessionId);
                if (session != null) {
                    RequestValues.setSession(session);
                }
            } catch (SessionNotFoundException snfe) {
                // swallow the exception - response will set the cookie to expire
                System.err.println("WARNING: Session ID in cookie " + sessionId + " could not be found");
            }
        }
    }

    private String getSessionCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().toLowerCase().equals(RequestValues.SESSION_COOKIE_NAME.toLowerCase())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    /**
     * Update the session cookie with a new or expired max-age unless this is a login or logout request. If it is a
     * login or logout request, allow the session service to handle adding the cookie.
     *
     * @param servletRequest the servlet request
     */
    private void setSessionCookieInResponseIfNotLoggingInOrOut(ServletRequest servletRequest) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if (!requestURI.contains("login") && !requestURI.contains("logout")) {
            RequestValues.setSessionCookieHeaderInResponse();
        }
    }

    private boolean isSecure() {
        return !Environments.DEV.getValue().equals(environment);
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Required
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
