package com.thistroll.server;

import com.thistroll.domain.Session;
import com.thistroll.domain.enums.Environments;
import org.joda.time.DateTime;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Static utility class containing request-scoped values
 *
 * Created by MVW on 9/5/2017.
 */
public class RequestValues {

    public static final String SESSION_COOKIE_NAME = "TT_Session";
    private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletResponse> httpServletResponseThreadLocal = new ThreadLocal<>();

    public static void setSession(Session session) {
        sessionThreadLocal.set(session);
    }

    public static Session getSession() {
        return sessionThreadLocal.get();
    }

    public static void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        httpServletResponseThreadLocal.set(httpServletResponse);
    }

    /**
     * After setting cookie in response, deletes the session from the thread local so that it will not be reused
     */
    public static void setSessionCookieHeaderInResponse() {
        HttpServletResponse httpServletResponse = httpServletResponseThreadLocal.get();
        Session session = sessionThreadLocal.get();
        Cookie cookie = createSessionCookie(session);
        httpServletResponse.addCookie(cookie);
        setSession(null);
    }

    /**
     * If a session cookie is already set on the response object, this retrieves it and updates it. Otherwise this
     * creates a new cookie.
     *
     * If the provided session is null, this will return a cookie with max-age of 0 (self-expiring)
     *
     * @param session the session
     * @return a session cookie
     */
    private static Cookie createSessionCookie(Session session) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, "");
        if (session == null) {
            // if there is no active session, expire the session cookie
            cookie.setPath("/");
            cookie.setValue("expired");
            cookie.setMaxAge(0);
        } else {
            int expirationTime = (int)((session.getExpirationTime().getMillis() - new DateTime().getMillis()) / 1000);
            cookie.setValue(session.getId());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setSecure(isSecureCookie());
            cookie.setMaxAge(expirationTime);
        }
        return cookie;
    }

    /**
     * Returns true if there is no system "env" variable or if it is set to anything other than "dev"
     *
     * @return true if there is no system "env" variable or if it is set to anything other than "dev"
     */
    private static boolean isSecureCookie() {
        return !Environments.DEV.getValue().equals(System.getProperties().getProperty("env"));
    }
}
