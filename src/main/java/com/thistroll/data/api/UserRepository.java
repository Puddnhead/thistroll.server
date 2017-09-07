package com.thistroll.data.api;

import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;

/**
 * API for CRUD operations on {@link com.thistroll.domain.User} objects
 *
 * Created by MVW on 8/26/2017.
 */
public interface UserRepository {

    /**
     * Create a user with the given password. Password will be stored as a hash and inaccessible via the public API
     *
     * @param user the user to create
     * @param password the password to use for this user
     * @return the created user with generated ID and date fields
     */
    User createUser(User user, String password);

    /**
     * Returns a user with the given id
     *
     * @param id user id
     * @return a user with the given id or null
     */
    User getUserById(String id);

    /**
     * Returns a user with the given username
     *
     * @param username the username
     * @return a user with the given username or null
     */
    User getUserByUsername(String username);

    /**
     * Update the given user
     *
     * @param user the user to update
     * @return the updated user with generated date fields
     * @throws IllegalArgumentException if no user with the provided ID exists
     */
    User updateUser(User user);

    /**
     * Delete the user with the given id
     *
     * @param id user id
     * @return success if the user was deleted else failure
     */
    Outcome deleteUser(String id);

    /**
     * Returns a user matching the provided credentials. If the there is no user with a given username and password,
     * returns null
     *
     * @param username the username
     * @param password the password
     * @return a user if one exists for the given credentials else null
     */
    User getUserWithCredentials(String username, String password);
}
