package com.thistroll.server.filters;

import com.thistroll.domain.Session;
import com.thistroll.server.RequestValues;
import com.thistroll.service.client.SessionService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link SessionFilter}
 * Not a strict unit test, as the RequestValues utility class is not mocked out
 *
 * Created by MVW on 9/6/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionFilterTest {

    @InjectMocks
    private SessionFilter sessionFilter;

    @Mock
    private SessionService sessionService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Session session;

    @Captor
    private ArgumentCaptor<Cookie> cookieArgumentCaptor;

    private Cookie[] cookies;

    private static final String SESSION_ID = "abc";

    private final DateTime IN_TEN_MINUTES = new DateTime(new DateTime().getMillis() + 60000);

    private Cookie sessionCookie = new Cookie(RequestValues.SESSION_COOKIE_NAME, SESSION_ID);

    private final String URL = "http://www.abc.com/statuscheck";

    @Before
    public void setup() throws Exception {
        when(session.getId()).thenReturn(SESSION_ID);
        when(session.getExpirationTime()).thenReturn(IN_TEN_MINUTES);
        when(httpServletRequest.getRequestURI()).thenReturn(URL);
    }

    @Test
    public void testSavesSessionInRequestValues() throws Exception {
        cookies = new Cookie[] { sessionCookie };
        when(httpServletRequest.getCookies()).thenReturn(cookies);
        when(sessionService.getSession(SESSION_ID)).thenReturn(session);

        sessionFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        assertThat(RequestValues.getSession(), is(session));
    }

    @Test
    public void testDoesNothingForNoSessionCookie() throws Exception {
        sessionFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        assertThat(RequestValues.getSession(), is(nullValue()));
    }

    @Test
    public void testSetsExpiringSessionCookieIfSessionCannotBeFound() throws Exception {
        cookies = new Cookie[] { sessionCookie };
        when(httpServletRequest.getCookies()).thenReturn(cookies);
        doNothing().when(httpServletResponse).addCookie(cookieArgumentCaptor.capture());

        sessionFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        Cookie cookie = cookieArgumentCaptor.getValue();
        assertThat(cookie.getMaxAge(), is(0));
    }

    @Test
    public void testDoesntAddSetCookieHeaderForLoginRequests() throws Exception {
        when(httpServletRequest.getRequestURI()).thenReturn("http://abc.com/login");
        sessionFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        verify(httpServletResponse, never()).addCookie(any(Cookie.class));
    }

    @Test
    public void testDoesntAddSetCookieHeaderForLogoutRequests() throws Exception {
        when(httpServletRequest.getRequestURI()).thenReturn("http://abc.com/logout");
        sessionFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        verify(httpServletResponse, never()).addCookie(any(Cookie.class));
    }
}