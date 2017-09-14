package com.thistroll.service.rest;

import com.thistroll.data.api.UserRepository;
import com.thistroll.domain.User;
import com.thistroll.server.RequestValues;
import com.thistroll.service.client.dto.request.LoginRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Rest Layer tests for {@link com.thistroll.service.client.SessionService}
 *
 * Created by MVW on 9/6/2017.
 */
public class SessionServiceControllerTest extends ControllerTestBase {

    @Autowired
    private UserRepository userRepository;

    private HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String USERNAME = "SamBradford";
    private static final String EMAIL = "sammy@vikings.com";
    private static final String PASSWORD = "password123";
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest(USERNAME, PASSWORD);

    @Before
    public void setup() throws Exception {
        RequestValues.setHttpServletResponse(httpServletResponse);
    }

    @Test
    public void testLogin() throws  Exception {
        when(userRepository.getUserWithCredentials(USERNAME, PASSWORD)).thenReturn(createUser());

        String serializedLoginRequest = objectMapper.writeValueAsString(LOGIN_REQUEST);
        MvcResult mvcResult = mockMvc.perform(post("/session/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedLoginRequest))
                .andExpect(status().isOk())
                .andReturn();
        String resultBody = mvcResult.getResponse().getContentAsString();
        assertThat(resultBody.contains(USER_ID), is(true));
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(post("/session/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private User createUser() {
        return new User.Builder()
                .id(USER_ID)
                .username(USERNAME)
                .email(EMAIL)
                .build();
    }
}