package com.thistroll.service.impl;

import com.thistroll.data.api.UserRepository;
import com.thistroll.domain.User;
import com.thistroll.service.client.UserService;
import com.thistroll.service.client.dto.CreateUserRequest;
import com.thistroll.service.exceptions.UserNotFoundException;
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

    @Required
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
