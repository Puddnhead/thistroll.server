package com.thistroll.service.impl;

import com.google.common.collect.Sets;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.thistroll.data.api.UserRepository;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.UserRole;
import com.thistroll.service.client.dto.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by MVW on 8/27/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> passwordCaptor;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    public static final String USERNAME = "JoeDirt";
    public static final String FIRST_NAME = "Joseph";
    public static final String LAST_NAME = "Dirt";
    public static final String PASSWORD = "password123";
    public static final String EMAIL = "joe@dirt.com";
    public static final Set<UserRole> ROLES = Sets.newHashSet(UserRole.ADMIN, UserRole.USER);
    public static final boolean NOTIFICATIONS_ENABLED = true;

    @Before
    public void setup() throws Exception {
        when(userRepository.createUser(userArgumentCaptor.capture(), passwordCaptor.capture())).thenReturn(mockUser);
        when(userRepository.getUserById(idCaptor.capture())).thenReturn(mockUser);
    }

    @Test
    public void testCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest.Builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .roles(ROLES)
                .notificationsEnabled(NOTIFICATIONS_ENABLED)
                .password(PASSWORD)
                .build();
        User createdUser = userService.createUser(request);
        User providatedUser = userArgumentCaptor.getValue();

        assertThat(createdUser, is(mockUser));
        assertThat(passwordCaptor.getValue(), is(PASSWORD));
        assertThat(providatedUser.getEmail(), is(EMAIL));
        assertThat(providatedUser.getUsername(), is(USERNAME));
        assertThat(providatedUser.getFirstName(), is(FIRST_NAME));
        assertThat(providatedUser.getLastName(), is(LAST_NAME));
        assertThat(providatedUser.getRoles(), is(ROLES));
        assertThat(providatedUser.isNotificationsEnabled(), is(NOTIFICATIONS_ENABLED));
    }

    @Test
    public void testGetUserById() throws Exception {
        String userId = "blah";
        User user = userService.getUserById(userId);
        assertThat(idCaptor.getValue(), is(userId));
        assertThat(user, is(mockUser));
    }
}