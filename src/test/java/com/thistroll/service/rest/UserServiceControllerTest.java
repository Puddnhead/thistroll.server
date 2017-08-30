package com.thistroll.service.rest;

import com.thistroll.data.api.UserRepository;
import com.thistroll.domain.User;
import com.thistroll.service.client.dto.CreateUserRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * REST Layer Test for {@link UserServiceController}
 *
 * Created by MVW on 8/29/2017.
 */
public class UserServiceControllerTest extends ControllerTestBase {

    @Autowired
    private UserRepository userRepository;

    public static final String GENERATED_ID = UUID.randomUUID().toString();
    public static final String USERNAME = "SamBradford";
    public static final String EMAIL = "sammy@vikings.com";
    public static final String PASSWORD = "password123";

    @Test
    public void testCreateUser() throws Exception {
        when(userRepository.createUser(any(User.class), anyString())).thenReturn(createUser());

        CreateUserRequest createUserRequest = new CreateUserRequest.Builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        String serializedCreateUserRequest = objectMapper.writeValueAsString(createUserRequest);
        MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedCreateUserRequest))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody.contains(GENERATED_ID), is(true));
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userRepository.getUserById(GENERATED_ID)).thenReturn(createUser());

        MvcResult mvcResult = mockMvc.perform(get("/user/" + GENERATED_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody.contains(GENERATED_ID), is(true));
    }

    @Test
    public void testGetUserByUsername() throws Exception {
        when(userRepository.getUserByUsername(USERNAME)).thenReturn(createUser());

        MvcResult mvcResult = mockMvc.perform(get("/user/username/" + USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody.contains(GENERATED_ID), is(true));
    }

    private User createUser() {
        return new User.Builder()
                .id(GENERATED_ID)
                .username(USERNAME)
                .email(EMAIL)
                .build();
    }
}