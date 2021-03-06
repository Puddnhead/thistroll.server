package com.thistroll.service.client;

import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.dto.request.CreateUserRequest;
import com.thistroll.service.client.dto.request.GetUserByEmailRequest;
import com.thistroll.service.client.dto.request.RegisterUserRequest;
import com.thistroll.service.client.dto.request.UpdateUserRequest;

import java.util.List;
import java.util.Optional;

/**
 * Service for CRUD operations on users
 *
 * Created by MVW on 8/26/2017.
 */
public interface UserService {

    /**
     * Register a user
     *
     * @param registerUserRequest the registration request, omitting roles but including a plaintext password
     * @return the generated user, including IDs and generated dates but omitting the password
     */
    User registerUser(RegisterUserRequest registerUserRequest);

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
     * @return the user
     * @throws com.thistroll.exceptions.UserNotFoundException if the user cannot be found
     */
    User getUserById(String id);

    /**
     * Find a user by username
     *
     * @param username the username
     * @return the user
     * @throws com.thistroll.exceptions.UserNotFoundException if the user cannot be found
     */
    User getUserByUsername(String username);

    /**
     * Find a user by email
     *
     * @param request the get user by email request
     * @return the user
     * @throws com.thistroll.exceptions.UserNotFoundException if the user cannot be found
     */
    User getUserByEmail(GetUserByEmailRequest request);

    /**
     * Find a user with a given username and password. Returns null if a user cannot be found or the password is
     * incorrect
     *
     * @param username the username
     * @param password the password
     * @return a user if the credentials are valid else null
     */
    User getUserWithCredentials(String username, String password);

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

    /**
     * Fetch all users. If pageNumber is omitted, pageSize is ignored and all users are fetched
     *
     * @param pageNumber optional page number
     * @param pageSize optional page size - ignored if page number is omitted
     * @return a list of users
     */
    List<User> getAllUsers(Optional<Integer> pageNumber, Optional<Integer> pageSize);

    /**
     * Fetch a comma-delimited list of email addresses (for easy paste in an email client) for all the users who have
     * notifications enabled
     *
     * @return a comma-delimited list of emails
     */
    String getEmailsForUsersWithNotificationsEnabled();
}
