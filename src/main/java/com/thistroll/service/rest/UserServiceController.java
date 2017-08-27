package com.thistroll.service.rest;

import com.thistroll.domain.User;
import com.thistroll.service.client.UserService;
import com.thistroll.service.client.dto.CreateUserRequest;
import org.springframework.beans.factory.annotation.Required;
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

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
