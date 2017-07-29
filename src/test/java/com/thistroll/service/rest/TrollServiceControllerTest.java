package com.thistroll.service.rest;

import com.amazonaws.util.StringUtils;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Tests for troll service
 *
 * Created by MVW on 7/13/2017.
 */
public class TrollServiceControllerTest extends ControllerTestBase {

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
}