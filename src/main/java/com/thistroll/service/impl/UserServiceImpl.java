package com.thistroll.service.impl;

import com.thistroll.data.api.UserRepository;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.UserService;
import com.thistroll.service.client.dto.CreateUserRequest;
import com.thistroll.service.client.dto.UpdateUserRequest;
import com.thistroll.service.exceptions.UserNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

/**
 * Created by MVW on 8/26/2017.
 */
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

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

    @Override
    public User getUserById(String id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Could not locate user with id: " + id);
        }

        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("Could not locate user with username: " + username);
        }

        return user;
    }

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

    @Override
    public Outcome deleteUser(String userId) {
        return userRepository.deleteUser(userId);
    }

    @Required
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
