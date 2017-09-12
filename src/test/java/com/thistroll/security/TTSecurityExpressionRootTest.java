package com.thistroll.security;

import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.UserRole;
import com.thistroll.server.RequestValues;
import org.junit.After;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link TTSecurityExpressionRoot}
 *
 * Created by MVW on 9/11/2017.
 */
public class TTSecurityExpressionRootTest {

    private Authentication mockAuthentication = mock(Authentication.class);
    private TTSecurityExpressionRoot expressionRoot = new TTSecurityExpressionRoot(mockAuthentication);

    @After
    public void cleanup() throws Exception {
        RequestValues.setSession(null);
    }

    @Test
    public void testIsAdminReturnsFalseForNoSession() throws Exception {
        assertThat(expressionRoot.isAdmin(), is(false));
    }

    @Test
    public void testIsAdminReturnsFalseForUserWithoutAdminRole() throws Exception {
        User user = mock(User.class);
        when(user.getRoles()).thenReturn(Collections.singleton(UserRole.USER));
        Session session = mock(Session.class);
        when(session.getUserDetails()).thenReturn(user);
        RequestValues.setSession(session);
        assertThat(expressionRoot.isAdmin(), is(false));
    }

    @Test
    public void testIsAdminReturnsTrueForUserWithAdminRole() throws Exception {
        User user = mock(User.class);
        when(user.getRoles()).thenReturn(Collections.singleton(UserRole.ADMIN));
        Session session = mock(Session.class);
        when(session.getUserDetails()).thenReturn(user);
        RequestValues.setSession(session);
        assertThat(expressionRoot.isAdmin(), is(true));
    }

}