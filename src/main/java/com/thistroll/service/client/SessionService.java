package com.thistroll.service.client;

import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.dto.request.LoginRequest;
import com.thistroll.service.exceptions.InvalidCredentialsException;

/**
 * Service for CRUD operations on Sessions, login, and logout
 *
 * Created by MVW on 9/3/2017.
 */
public interface SessionService {

    /**
     * Service for logging into the application
     *
     * @param loginRequest the login request containing user credentials
     * @return a session if the credentials provided were valid
     * @throws org.apache.http.auth.InvalidCredentialsException if the credentials are invalid
     */
    Session login(LoginRequest loginRequest) throws InvalidCredentialsException;

    /**
     * Service for logging out of the application. Uses session information from the RequestValues static utility class.
     * This service will not generate errors for sessions that cannot be found.
     *
     * @return success if a session was found else failure
     */
    Outcome logout();

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
     * Deletes the provided session. This service will not generate errors for sessions that cannot be found.
     *
     * @param sessionId session id
     * @return success if a session could be found else failure
     */
    Outcome deleteSession(String sessionId);
}
