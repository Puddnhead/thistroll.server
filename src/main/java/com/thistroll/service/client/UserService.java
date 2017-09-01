package com.thistroll.service.client;

import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.dto.CreateUserRequest;
import com.thistroll.service.client.dto.UpdateUserRequest;

/**
 * Service for CRUD operations on users
 *
 * Created by MVW on 8/26/2017.
 */
public interface UserService {

    /**
     * Create a user
     *
     * @param createUserRequest the request to create the user, including a plain-text password
     * @return the generated user, including IDs and generated dates but omitting the password
     */
    User createUser(CreateUserRequest createUserRequest);

    /**
     * Find a user by user id
     *
     * @param id the user id
     * @return the user or null if it cannot be found
     */
    User getUserById(String id);

    /**
     * Find a user by username
     *
     * @param username the username
     * @return the user or null if it cannot be found
     */
    User getUserByUsername(String username);

    /**
     * Update the following fields of a user:
     *      firstName
     *      lastName
     *      email
     *      notificationsEnabled
     *
     * @param request the request containing the fields to upadte. null values are ignored.
     * @return the updated user
     */
    User updateUser(UpdateUserRequest request);

    /**
     * Guess
     *
     * @param userId guess
     * @return success if the user was deleted otherwise failure
     */
    Outcome deleteUser(String userId);
}
