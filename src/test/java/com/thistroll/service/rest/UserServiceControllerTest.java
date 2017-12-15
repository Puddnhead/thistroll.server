package com.thistroll.service.rest;

import com.thistroll.data.api.UserRepository;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.dto.request.CreateUserRequest;
import com.thistroll.service.client.dto.request.GetUserByEmailRequest;
import com.thistroll.service.client.dto.request.RegisterUserRequest;
import com.thistroll.service.client.dto.request.UpdateUserRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * REST Layer Test for {@link UserServiceController}
 *
 * Created by MVW on 8/29/2017.
 */
public class UserServiceControllerTest extends ControllerTestBase {

    @Autowired
    private UserRepository userRepository;

    private static final String GENERATED_ID = UUID.randomUUID().toString();
    private static final String USERNAME = "SamBradford";
    private static final String EMAIL = "sammy@vikings.com";
    private static final String PASSWORD = "password123";

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
    public void testRegisterUser() throws Exception {
        when(userRepository.createUser(any(User.class), anyString())).thenReturn(createUser());

        RegisterUserRequest request = new RegisterUserRequest.Builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        String serializedCreateUserRequest = objectMapper.writeValueAsString(request);
        MvcResult mvcResult = mockMvc.perform(post("/user/register")
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

    @Test
    public void testUpdateUser() throws Exception {
        when(userRepository.updateUser(any(User.class))).thenReturn(createUser());
        when(userRepository.getUserById(anyString())).thenReturn(createUser());

        UpdateUserRequest request = new UpdateUserRequest.Builder()
                .email(EMAIL)
                .userId(GENERATED_ID)
                .build();
        String serializedUpdateUserRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedUpdateUserRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(userRepository.deleteUser(anyString())).thenReturn(Outcome.SUCCESS);

        mockMvc.perform(delete("/user/someId"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userRepository.getAllUsers(Optional.empty(), Optional.empty())).thenReturn(Collections.singletonList(createUser()));

        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEmailsForUsersWithNotificationsEnabled() throws Exception {
        when(userRepository.getAllUsers(Optional.empty(), Optional.empty())).thenReturn(Collections.singletonList(createUser()));

        mockMvc.perform(get("/user/notifications/email"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserByEmailAddress() throws Exception {
        when(userRepository.getUserByEmail(EMAIL)).thenReturn(createUser());

        GetUserByEmailRequest request = new GetUserByEmailRequest(EMAIL);
        String serializedRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/user/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedRequest))
                .andExpect(status().isOk());
    }

    private User createUser() {
        return new User.Builder()
                .id(GENERATED_ID)
                .username(USERNAME)
                .email(EMAIL)
                .build();
    }
}