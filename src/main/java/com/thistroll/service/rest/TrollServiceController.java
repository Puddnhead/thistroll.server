package com.thistroll.service.rest;

import com.thistroll.service.client.TrollService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Rest layer from {@link TrollService}
 *
 * Created by MVW on 7/21/2017.
 */
@Controller
@RequestMapping("/troll")
public class TrollServiceController implements TrollService {

    private TrollService trollService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Override
    public @ResponseBody String trollSpeak(@RequestBody String statement) {
        return trollService.trollSpeak(statement);
    }

    @Required
    public void setTrollService(TrollService trollService) {
        this.trollService = trollService;
    }
}
