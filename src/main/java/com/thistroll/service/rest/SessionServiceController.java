package com.thistroll.service.rest;

import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.SessionService;
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

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Override
    public @ResponseBody Session createSessionByUserId(@RequestParam String userId) {
        return sessionService.createSessionByUserId(userId);
    }

    @Override
    public @ResponseBody Session createSessionForUser(User user) {
        throw new NotImplementedException("Not implemented");
    }

    @RequestMapping(value = "/{sessionId}", method = RequestMethod.GET)
    @Override
    public @ResponseBody Session getSession(@PathVariable String sessionId) {
        return sessionService.getSession(sessionId);
    }

    @RequestMapping(value ="/{sessionId}", method = RequestMethod.DELETE)
    @Override
    public @ResponseBody Outcome deleteSession(@PathVariable String sessionId) {
        return sessionService.deleteSession(sessionId);
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
