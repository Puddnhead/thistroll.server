package com.thistroll.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.SessionService;
import com.thistroll.service.client.UserService;
import com.thistroll.service.exceptions.SessionNotFoundException;
import com.thistroll.service.exceptions.UserNotFoundException;
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

    private Cache<String, Session> sessionCache = null;

    private UserService userService;

    @Override
    public Session createSessionByUserId(String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("Could not create a session for user with id " + userId
                    + "because the user could not be found");
        }
        return createSessionForUser(user);
    }

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

    @Override
    public Outcome deleteSession(String sessionId) {
        Cache cache = getSessionCache();
        Session session = (Session)cache.getIfPresent(sessionId);
        if (session == null) {
            throw new SessionNotFoundException("Session with id " + sessionId + " does not exist or is expired");
        }

        cache.invalidate(sessionId);
        return Outcome.SUCCESS;
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
