package com.thistroll.service.client;

import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;

/**
 * Service for CRUD operations on Sessions
 *
 * Created by MVW on 9/3/2017.
 */
public interface SessionService {

    /**
     * Create a session by user ID. This will only be used for external testing. In the application sessions are all
     * created internally through the login service. The "Create" methods should not be externally accessible.
     *
     * @param userId the user id
     * @return a session
     */
    Session createSessionByUserId(String userId);

    /**
     * Create a session for the provided user
     *
     * @param user the user
     * @return the session
     */
    Session createSessionForUser(User user);

    /**
     * Retrieve the session with the given id. If the session has expired the service throws a SessionNotFound error.
     * This method internally updates the session expiration
     *
     * @param sessionId the session id
     * @return the session
     * @throws com.thistroll.service.exceptions.SessionNotFoundException
     */
    Session getSession(String sessionId);

    /**
     * Deletes the provided session.
     *
     * @param sessionId session id
     * @return success
     * @throws com.thistroll.service.exceptions.SessionNotFoundException if the session cannot be found
     */
    Outcome deleteSession(String sessionId);
}
