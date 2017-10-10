package com.thistroll.service.impl;

import com.thistroll.data.api.UserRepository;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.domain.enums.UserRole;
import com.thistroll.server.logging.ThrowsError;
import com.thistroll.server.logging.ThrowsWarning;
import com.thistroll.service.client.UserService;
import com.thistroll.service.client.dto.request.CreateUserRequest;
import com.thistroll.service.client.dto.request.GetUserByEmailRequest;
import com.thistroll.service.client.dto.request.RegisterUserRequest;
import com.thistroll.service.client.dto.request.UpdateUserRequest;
import com.thistroll.exceptions.UserNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation class for {@link UserService}
 *
 * Created by MVW on 8/26/2017.
 */
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @ThrowsError
    @Override
    public User registerUser(RegisterUserRequest registerUserRequest) {
        User user = new User.Builder()
                .roles(Collections.singleton(UserRole.USER))
                .email(registerUserRequest.getEmail())
                .firstName(registerUserRequest.getFirstName())
                .lastName(registerUserRequest.getLastName())
                .notificationsEnabled(registerUserRequest.isNotificationsEnabled())
                .username(registerUserRequest.getUsername())
                .build();
        return userRepository.createUser(user, registerUserRequest.getPassword());
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public User createUser(CreateUserRequest createUserRequest) {
        User user = new User.Builder()
                .username(createUserRequest.getUsername())
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .email(createUserRequest.getEmail())
                .roles(createUserRequest.getRoles())
                .notificationsEnabled(createUserRequest.isNotificationsEnabled())
                .build();
        return userRepository.createUser(user, createUserRequest.getPassword());
    }

    @PreAuthorize("isAdmin()")
    @ThrowsWarning
    @Override
    public User getUserById(String id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Could not locate user with id: " + id);
        }

        return user;
    }

    @PreAuthorize("isAdmin()")
    @ThrowsWarning
    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("Could not locate user with username: " + username);
        }

        return user;
    }

    @PreAuthorize("isAdmin()")
    @ThrowsWarning
    @Override
    public User getUserByEmail(GetUserByEmailRequest request) {
        User user = userRepository.getUserByEmail(request.getEmail());
        if (user == null) {
            throw new UserNotFoundException("Could not locate user with email: " + request.getEmail());
        }

        return user;
    }

    @ThrowsWarning
    @Override
    public User getUserWithCredentials(String username, String password) {
        return userRepository.getUserWithCredentials(username, password);
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public User updateUser(UpdateUserRequest request) {
        User previousState = null;

        if (StringUtils.isNotEmpty(request.getUserId())) {
            previousState = userRepository.getUserById(request.getUserId());
        } else if (StringUtils.isNotEmpty(request.getUsername())){
            previousState = userRepository.getUserByUsername(request.getUsername());
        }

        if (previousState == null) {
            throw new UserNotFoundException("Cannot update user because it could not be found");
        }

        User.Builder builder = new User.Builder()
                .id(previousState.getId())
                .username(previousState.getUsername())
                .lastUpdatedOn(new DateTime());

        if (StringUtils.isNotEmpty(request.getEmail())) {
            builder = builder.email(request.getEmail());
        }
        if (StringUtils.isNotEmpty(request.getFirstName())) {
            builder = builder.firstName(request.getFirstName());
        }
        if (StringUtils.isNotEmpty(request.getLastName())) {
            builder = builder.lastName(request.getLastName());
        }
        if (request.isNotificationsEnabled() != null) {
            builder = builder.notificationsEnabled(request.isNotificationsEnabled());
        } else {
            builder = builder.notificationsEnabled(previousState.isNotificationsEnabled());
        }

        return userRepository.updateUser(builder.build());
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public Outcome deleteUser(String userId) {
        return userRepository.deleteUser(userId);
    }

    @PreAuthorize(("isAdmin()"))
    @ThrowsError
    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @PreAuthorize(("isAdmin()"))
    @ThrowsError
    @Override
    public String getEmailsForUsersWithNotificationsEnabled() {
        List<String> emails = getAllUsers().stream()
                .filter(User::isNotificationsEnabled)
                .map(User::getEmail)
                .collect(Collectors.toList());
        return StringUtils.join(emails, ',');
    }

    @Required
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
