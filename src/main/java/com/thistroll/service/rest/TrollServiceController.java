package com.thistroll.service.rest;

import com.thistroll.domain.Speech;
import com.thistroll.domain.enums.Outcome;
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
    public @ResponseBody String trollSpeak(@RequestBody String speech) {
        return trollService.trollSpeak(speech);
    }

    @RequestMapping(value = "/speech", method = RequestMethod.POST)
    @Override
    public @ResponseBody Speech getSpeechByText(@RequestBody String text) {
        return trollService.getSpeechByText(text);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @Override
    public @ResponseBody Outcome deleteSpeech(@RequestBody String idOrText) {
        return trollService.deleteSpeech(idOrText);
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    @Override
    public @ResponseBody Speech getNextSpeechWithNoResponse() {
        return trollService.getNextSpeechWithNoResponse();
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @Override
    public @ResponseBody Speech updateResponses(@RequestBody Speech speech) {
        return trollService.updateResponses(speech);
    }

    @RequestMapping(value = "/noresponses/count", method = RequestMethod.GET)
    @Override
    public @ResponseBody int getSpeechWithoutResponsesCount() {
        return trollService.getSpeechWithoutResponsesCount();
    }

    @RequestMapping(value = "/knownspeech/count", method = RequestMethod.GET)
    @Override
    public @ResponseBody int getKnownSpeechCount() {
        return trollService.getKnownSpeechCount();
    }

    @Required
    public void setTrollService(TrollService trollService) {
        this.trollService = trollService;
    }
}
