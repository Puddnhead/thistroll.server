package com.thistroll.service.rest;

import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.server.RequestValues;
import com.thistroll.service.client.SessionService;
import com.thistroll.service.client.dto.LoginRequest;
import com.thistroll.service.exceptions.InvalidCredentialsException;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Rest layer for {@link com.thistroll.service.client.SessionService}
 * Only used for testing. None of these methods should be exposed externally.
 *
 * Created by MVW on 9/3/2017.
 */
@Controller
@RequestMapping("/session")
public class SessionServiceController implements SessionService {

    private SessionService sessionService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @Override
    public @ResponseBody Session login(@RequestBody LoginRequest loginRequest) throws InvalidCredentialsException {
        Session session = sessionService.login(loginRequest);
        RequestValues.setSession(session);
        RequestValues.setSessionCookieHeaderInResponse();
        return session;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @Override
    public @ResponseBody Outcome logout() {
        return sessionService.logout();
    }

    // Only exposed during testing
    // @RequestMapping(value = "", method = RequestMethod.POST)
    @Override
    public @ResponseBody Session createSessionByUserId(@RequestParam String userId) {
        return sessionService.createSessionByUserId(userId);
    }

    @Override
    public @ResponseBody Session createSessionForUser(User user) {
        throw new NotImplementedException("Not implemented");
    }

    // Only exposed during testing
    // @RequestMapping(value = "/{sessionId}", method = RequestMethod.GET)
    @Override
    public @ResponseBody Session getSession(@PathVariable String sessionId) {
        return sessionService.getSession(sessionId);
    }

    // Only exposed during testing
    // @RequestMapping(value ="/{sessionId}", method = RequestMethod.DELETE)
    @Override
    public @ResponseBody Outcome deleteSession(@PathVariable String sessionId) {
        return sessionService.deleteSession(sessionId);
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
