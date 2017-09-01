package com.thistroll.service.rest;

import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.UserService;
import com.thistroll.service.client.dto.CreateUserRequest;
import com.thistroll.service.client.dto.UpdateUserRequest;
import com.thistroll.service.exceptions.DeleteFailedException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Rest layer of {@link UserService}
 * Created by MVW on 8/26/2017.
 */
@Controller
@RequestMapping("/user")
public class UserServiceController implements UserService {

    private UserService userService;

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

    @RequestMapping(value ="/username/{username}", method = RequestMethod.GET)
    @Override
    public @ResponseBody User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @RequestMapping(value="", method = RequestMethod.PUT)
    @Override
    public @ResponseBody User updateUser(@RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }

    @RequestMapping(value="/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Override
    public @ResponseBody Outcome deleteUser(@PathVariable String userId) {
        Outcome outcome = userService.deleteUser(userId);
        if (outcome == Outcome.FAILURE) {
            throw new DeleteFailedException("Could not delete user with id " + userId);
        }

        return outcome;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
