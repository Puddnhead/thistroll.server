package com.thistroll.service.rest;

import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.exceptions.DeleteFailedException;
import com.thistroll.service.client.UserService;
import com.thistroll.service.client.dto.request.CreateUserRequest;
import com.thistroll.service.client.dto.request.GetUserByEmailRequest;
import com.thistroll.service.client.dto.request.RegisterUserRequest;
import com.thistroll.service.client.dto.request.UpdateUserRequest;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Rest layer of {@link UserService}
 * Created by MVW on 8/26/2017.
 */
@Controller
@RequestMapping("/user")
public class UserServiceController implements UserService {

    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @Override
    public @ResponseBody User registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        return userService.registerUser(registerUserRequest);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Override
    public @ResponseBody User createUser(@RequestBody CreateUserRequest createUserRequest) {
        return userService.createUser(createUserRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public @ResponseBody User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @RequestMapping(value = "/username/{username}", method = RequestMethod.GET)
    @Override
    public @ResponseBody User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    @Override
    public @ResponseBody User getUserByEmail(@RequestBody GetUserByEmailRequest request) {
        return userService.getUserByEmail(request);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @Override
    public @ResponseBody User updateUser(@RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }

    @RequestMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public @ResponseBody Outcome deleteUser(@PathVariable String userId) {
        Outcome outcome = userService.deleteUser(userId);
        if (outcome == Outcome.FAILURE) {
            throw new DeleteFailedException("Could not delete user with id " + userId);
        }

        return outcome;
    }

    @RequestMapping(value = "/all")
    @Override
    public @ResponseBody List<User> getAllUsers(@RequestParam Optional<Integer> pageNumber, @RequestParam Optional<Integer> pageSize) {
        return userService.getAllUsers(pageNumber, pageSize);
    }

    @RequestMapping(value = "/notifications/email")
    @Override
    public @ResponseBody String getEmailsForUsersWithNotificationsEnabled() {
        return userService.getEmailsForUsersWithNotificationsEnabled();
    }

    @Override
    public User getUserWithCredentials(String username, String password) {
        throw new NotImplementedException("Unimplemented");
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
