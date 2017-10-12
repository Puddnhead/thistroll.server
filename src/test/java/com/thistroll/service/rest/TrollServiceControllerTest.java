package com.thistroll.service.rest;

import com.amazonaws.util.StringUtils;
import com.thistroll.data.api.SpeechRepository;
import com.thistroll.domain.Speech;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Tests for troll service
 *
 * Created by MVW on 7/13/2017.
 */
public class TrollServiceControllerTest extends ControllerTestBase {

    @Autowired
    @Qualifier("com.thistroll.data.knownspeechesrepository")
    SpeechRepository knownSpeechRepository;

    @Test
    public void testTrollSpeak() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/troll")
                .contentType(MediaType.TEXT_PLAIN)
                .content("what's your name?"))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(StringUtils.isNullOrEmpty(responseBody), is(false));
    }

    @Test
    public void testGetNextSpeechWithoutAResponse() throws Exception {
        mockMvc.perform(get("/troll/next"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateSpeech() throws Exception {
        Speech updateRequest = new Speech.Builder()
                .id("someId")
                .responses(Collections.singletonList("some response"))
                .build();
        String serializedRequest = objectMapper.writeValueAsString(updateRequest);

        when(knownSpeechRepository.saveSpeech(any(Speech.class))).thenReturn(updateRequest);

        MvcResult mvcResult = mockMvc.perform(put("/troll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedRequest))
                .andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertThat(result.contains("someId"), is(true));
    }

    @Test
    public void testGetSpeechByText() throws Exception {
        when(knownSpeechRepository.getSpeechByText(anyString()))
                .thenReturn(new Speech.Builder().id("someId").text("blah").build());

        MvcResult mvcResult = mockMvc.perform(post("/troll/speech")
                .contentType(MediaType.APPLICATION_JSON)
                .content("what's up"))
                .andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertThat(result.contains("someId"), is(true));
    }
    
    @Test
    public void testDeleteSpeech() throws Exception {
        mockMvc.perform(post("/troll/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("what's up"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetNoResponseCount() throws Exception {
        mockMvc.perform(get("/troll/noresponses/count"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKnownSpeechCount() throws Exception {
        mockMvc.perform(get("/troll/knownspeech/count"))
                .andExpect(status().isOk());
    }
}