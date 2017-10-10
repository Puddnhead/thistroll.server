package com.thistroll.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.server.RequestValues;
import com.thistroll.server.logging.ThrowsError;
import com.thistroll.server.logging.ThrowsWarning;
import com.thistroll.service.client.SessionService;
import com.thistroll.service.client.UserService;
import com.thistroll.service.client.dto.request.LoginRequest;
import com.thistroll.exceptions.InvalidCredentialsException;
import com.thistroll.exceptions.SessionNotFoundException;
import com.thistroll.exceptions.UserNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Serivce implementation for {@link SessionService}
 *
 * Created by MVW on 9/3/2017.
 */
public class SessionServiceImpl implements SessionService {

    private long sessionExpirationInMillis;

    private static Cache<String, Session> sessionCache = null;

    private UserService userService;

    @ThrowsWarning
    @Override
    public Session login(LoginRequest loginRequest) throws InvalidCredentialsException {
        User user = userService.getUserWithCredentials(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            throw new InvalidCredentialsException("The username or the password was incorrect");
        }
        return createSessionForUser(user);
    }

    @ThrowsError
    @Override
    public Outcome logout() {
        Outcome outcome = Outcome.FAILURE;
        Session session = RequestValues.getSession();

        if (session != null && StringUtils.isNotEmpty(session.getId())) {
            String sessionId = session.getId();
            outcome = deleteSession(sessionId);
        }

        RequestValues.setSession(null);
        RequestValues.setSessionCookieHeaderInResponse();

        return outcome;
    }

    @ThrowsError
    @Override
    public Session createSessionByUserId(String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("Could not create a session for user with id " + userId
                    + "because the user could not be found");
        }
        return createSessionForUser(user);
    }

    @ThrowsError
    @Override
    public Session createSessionForUser(User user) {
        Cache<String, Session> cache = getSessionCache();
        String sessionId = UUID.randomUUID().toString();
        DateTime now = new DateTime();

        Session session = new Session.Builder()
                .id(sessionId)
                .createdOn(now)
                .lastUpdatedOn(now)
                .userDetails(user)
                .expirationTime(new DateTime(now.getMillis() + sessionExpirationInMillis))
                .build();
        cache.put(sessionId, session);
        return session;
    }

    @ThrowsWarning
    @Override
    public Session getSession(String sessionId) {
        Cache cache = getSessionCache();
        Session oldSession = (Session)cache.getIfPresent(sessionId);
        if (oldSession == null) {
            throw new SessionNotFoundException("Session with id " + sessionId + " does not exist or is expired");
        }

        DateTime now = new DateTime();
        Session updatedSession = new Session.Builder()
                .id(sessionId)
                .createdOn(oldSession.getCreatedOn())
                .lastUpdatedOn(now)
                .userDetails(oldSession.getUserDetails())
                .expirationTime(new DateTime(now.getMillis() + sessionExpirationInMillis))
                .build();
        cache.put(sessionId, updatedSession);
        return updatedSession;
    }

    @ThrowsError
    @Override
    public Outcome deleteSession(String sessionId) {
        Outcome outcome;
        Cache cache = getSessionCache();
        Session session = (Session)cache.getIfPresent(sessionId);
        if (session != null) {
            cache.invalidate(sessionId);
            outcome = Outcome.SUCCESS;
        } else {
            outcome = Outcome.FAILURE;
        }

        return outcome;
    }

    /**
     * Use a factory method to ensure expiration time is injected before we create the cache
     *
     * @return the sessionCache
     */
    private Cache<String, Session> getSessionCache() {
        if (sessionCache == null) {
            sessionCache = CacheBuilder.newBuilder()
                    .expireAfterAccess(sessionExpirationInMillis, TimeUnit.MILLISECONDS)
                    .build();
        }
        return sessionCache;
    }

    @Required
    public void setSessionExpirationInMillis(long sessionExpirationInMillis) {
        this.sessionExpirationInMillis = sessionExpirationInMillis;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
