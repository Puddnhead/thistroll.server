package com.thistroll.service.impl;

import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.server.RequestValues;
import com.thistroll.service.client.UserService;
import com.thistroll.service.client.dto.request.LoginRequest;
import com.thistroll.exceptions.InvalidCredentialsException;
import com.thistroll.exceptions.SessionNotFoundException;
import com.thistroll.exceptions.UserNotFoundException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link SessionServiceImpl}
 *
 * Created by MVW on 9/3/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionServiceImplTest {

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Mock
    private UserService userService;

    @Mock
    private User mockUser;

    @Mock
    private HttpServletResponse httpServletResponse;

    private final String USERNAME = "litabeach";
    private final String PASSWORD = "notpresent";
    private final LoginRequest LOGIN_REQUEST = new LoginRequest(USERNAME, PASSWORD);

    @Before
    public void setup() throws Exception {
        // not really unit testy - interacts with a static class
        RequestValues.setHttpServletResponse(httpServletResponse);
        sessionService.setSessionExpirationInMillis(60000L);
    }

    @Test
    public void testLogin() throws Exception {
        DateTime now = new DateTime();
        when(userService.getUserWithCredentials(USERNAME, PASSWORD)).thenReturn(mockUser);
        Session session = sessionService.login(LOGIN_REQUEST);
        assertCreatedSessionFields(session, now);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testLoginWithInvalidCredentialsThrowsException() throws Exception {
        sessionService.login(LOGIN_REQUEST);
    }

    @Test
    public void testLogout() throws Exception {
        when(userService.getUserWithCredentials(USERNAME, PASSWORD)).thenReturn(mockUser);
        Session session = sessionService.login(LOGIN_REQUEST);

        // not really unit testy - interacts with a static utility class
        RequestValues.setSession(session);

        Outcome outcome = sessionService.logout();
        assertThat(outcome, is(Outcome.SUCCESS));
    }

    @Test
    public void testLogoutForSessionNotFoundReturnsFailure() throws Exception {
        Outcome outcome = sessionService.logout();
        assertThat(outcome, is(Outcome.FAILURE));
    }

    @Test
    public void testCreateUserByUserId() throws Exception {
        DateTime now = new DateTime();
        when(userService.getUserById(anyString())).thenReturn(mockUser);
        Session session = sessionService.createSessionByUserId("id");
        assertCreatedSessionFields(session, now);
    }

    @Test
    public void testCreateUser() throws Exception {
        DateTime now = new DateTime();
        Session session = sessionService.createSessionForUser(mockUser);
        assertCreatedSessionFields(session, now);
    }

    private void assertCreatedSessionFields(Session session, DateTime now) {
        assertThat(session, is(not(nullValue())));
        assertThat(session.getUserDetails(), is(mockUser));
        assertThat(session.getExpirationTime().getMillis() > now.getMillis(), is(true));
        assertThat(session.getCreatedOn(), is(not(nullValue())));
        assertThat(session.getLastUpdatedOn(), is(not(nullValue())));
    }

    @Test(expected = UserNotFoundException.class)
    public void testCreateUserByUserIdThrowsUserNotFoundException() throws Exception {
        sessionService.createSessionByUserId("id");
    }

    @Test
    public void testGetSession() throws Exception {
        Session session = sessionService.createSessionForUser(mockUser);
        // Sleep to ensure expiration time gets updated when we fetch
        Thread.sleep(1L);
        Session fetchedSession = sessionService.getSession(session.getId());
        assertThat(fetchedSession.getId(), is(session.getId()));
        assertThat(fetchedSession.getCreatedOn(), is(session.getCreatedOn()));
        assertThat(fetchedSession.getLastUpdatedOn().getMillis() > session.getLastUpdatedOn().getMillis(), is(true));
        assertThat(fetchedSession.getExpirationTime().getMillis() > session.getExpirationTime().getMillis(), is(true));
        assertThat(fetchedSession.getUserDetails(), is(session.getUserDetails()));
    }

    @Test(expected = SessionNotFoundException.class)
    public void testGetSessionThrowsSessionNotFoundException() throws Exception {
        sessionService.getSession("blah");
    }

    @Test
    public void testDeleteSession() throws Exception {
        Session session = sessionService.createSessionForUser(mockUser);
        Outcome outcome = sessionService.deleteSession(session.getId());
        assertThat(outcome, is(Outcome.SUCCESS));
    }

    public void testDeleteSessionReturnsFailureForExpiredSession() throws Exception {
        sessionService.setSessionExpirationInMillis(1);
        Session session = sessionService.createSessionForUser(mockUser);
        Thread.sleep(1L);
        Outcome outcome = sessionService.deleteSession(session.getId());
        assertThat(outcome, is(Outcome.FAILURE));
    }
}