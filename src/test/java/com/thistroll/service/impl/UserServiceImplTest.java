package com.thistroll.service.impl;

import com.google.common.collect.Sets;
import com.thistroll.data.api.UserRepository;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.UserRole;
import com.thistroll.exceptions.RecaptchaValidationException;
import com.thistroll.server.RecaptchaVerificationService;
import com.thistroll.service.client.dto.request.CreateUserRequest;
import com.thistroll.service.client.dto.request.RegisterUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link UserServiceImpl}
 * Created by MVW on 8/27/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecaptchaVerificationService recaptchaVerificationService;

    @Mock
    private User mockUser;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> passwordCaptor;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    private static final String USER_ID = "abc124";
    private static final String USERNAME = "JoeDirt";
    private static final String FIRST_NAME = "Joseph";
    private static final String LAST_NAME = "Dirt";
    private static final String PASSWORD = "password123";
    private static final String EMAIL = "joe@dirt.com";
    private static final Set<UserRole> ROLES = Sets.newHashSet(UserRole.ADMIN, UserRole.USER);
    private static final boolean NOTIFICATIONS_ENABLED = true;
    private static final String GRECAPTCHA_RESPONSE = "blah";

    @Before
    public void setup() throws Exception {
        when(userRepository.createUser(userArgumentCaptor.capture(), passwordCaptor.capture())).thenReturn(mockUser);
        when(userRepository.getUserById(idCaptor.capture())).thenReturn(mockUser);
        when(recaptchaVerificationService.verify(anyString())).thenReturn(true);
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
    public void testRegisterUser() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest.Builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .notificationsEnabled(NOTIFICATIONS_ENABLED)
                .password(PASSWORD)
                .grecaptchaResponse(GRECAPTCHA_RESPONSE)
                .build();
        User createdUser = userService.registerUser(request);
        User providatedUser = userArgumentCaptor.getValue();

        assertThat(createdUser, is(mockUser));
        assertThat(passwordCaptor.getValue(), is(PASSWORD));
        assertThat(providatedUser.getEmail(), is(EMAIL));
        assertThat(providatedUser.getUsername(), is(USERNAME));
        assertThat(providatedUser.getFirstName(), is(FIRST_NAME));
        assertThat(providatedUser.getLastName(), is(LAST_NAME));
        assertThat(providatedUser.getRoles(), is(Collections.singleton(UserRole.USER)));
        assertThat(providatedUser.isNotificationsEnabled(), is(NOTIFICATIONS_ENABLED));
    }

    @Test
    public void testGetEmailsForUsersWithNotificationsEnabled() throws Exception {
        when(userRepository.getAllUsers(Optional.empty(), Optional.empty()))
                .thenReturn(Arrays.asList(createUser(), createUser(), createUser()));
        String emailList = userService.getEmailsForUsersWithNotificationsEnabled();
        assertThat(emailList, is(EMAIL + "," + EMAIL + "," + EMAIL));
    }

    @Test(expected = RecaptchaValidationException.class)
    public void testInvalidCaptchaThrowsException() throws Exception {
        when(recaptchaVerificationService.verify(anyString())).thenReturn(false);
        userService.registerUser(new RegisterUserRequest.Builder().grecaptchaResponse("blah").build());
    }

    private User createUser() {
        return new User.Builder()
                .id(USER_ID)
                .username(USERNAME)
                .email(EMAIL)
                .notificationsEnabled(true)
                .build();
    }
}