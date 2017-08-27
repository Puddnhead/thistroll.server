package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.UserRole;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by MVW on 8/27/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryImplTest extends AbstractRepositoryTest {

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private static final String USERNAME = "BobbyBriscuit";
    private static final String FIRST_NAME = "Bobby";
    private static final String LAST_NAME = "Briscuit";
    private static final String EMAIL = "bobby@briscuit.com";
    private static final Set<UserRole> ROLES = Collections.singleton(UserRole.USER);
    private static final boolean NOTIFICATIONS_ENABLED = true;
    private static final String PASSWORD = "password123";

    @Override
    void setConnectionProvider(DynamoDBConnectionProvider dynamoDBConnectionProvider) {
        userRepository.setConnectionProvider(dynamoDBConnectionProvider);
    }

    @Test
    public void testCreateUserGeneratesIdAndDatesAndHashesPassword() throws Exception {
        final String IGNORED_ID = "abc";
        final DateTime IGNORED_DATE = new DateTime(0);

        User suppliedUser = new User.Builder()
                .id(IGNORED_ID)
                .createdOn(IGNORED_DATE)
                .lastUpdatedOn(IGNORED_DATE)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .roles(ROLES)
                .notificationsEnabled(NOTIFICATIONS_ENABLED)
                .email(EMAIL)
                .build();

        when(mockTable.putItem(itemCaptor.capture())).thenReturn(mock(PutItemOutcome.class));

        User user = userRepository.createUser(suppliedUser, PASSWORD);
        assertThat(user.getId(), is(not(IGNORED_ID)));
        assertThat(user.getCreatedOn(), is(not(IGNORED_DATE.getMillis())));
        assertThat(user.getLastUpdatedOn(), is(not(IGNORED_DATE.getMillis())));
        assertThat(user.getUsername(), is(USERNAME));
        assertThat(user.getFirstName(), is(FIRST_NAME));
        assertThat(user.getLastName(), is(LAST_NAME));
        assertThat(user.getEmail(), is(EMAIL));
        assertThat(user.getRoles(), is(ROLES));
        assertThat(user.isNotificationsEnabled(), is(NOTIFICATIONS_ENABLED));

        Item item = itemCaptor.getValue();
        assertThat(item.getString(User.PASSWORD_PROPERTY), is(not(PASSWORD)));
        assertThat(item.getString(User.ID_PROPERTY), is(not(IGNORED_ID)));
        assertThat(item.getLong(User.CREATED_ON_PROPERTY), is(not(IGNORED_DATE.getMillis())));
        assertThat(item.getLong(User.LAST_UPDATED_ON_PROPERTY), is(not(IGNORED_DATE.getMillis())));
        assertThat(item.getString(User.USERNAME_PROPERTY), is(USERNAME));
        assertThat(item.getString(User.FIRST_NAME_PROPERTY), is(FIRST_NAME));
        assertThat(item.getString(User.LAST_NAME_PROPERTY), is(LAST_NAME));
        assertThat(item.getString(User.EMAIL_PROPERTY), is(EMAIL));
        assertThat(item.getString(User.ROLES_PROPERTY), is(ROLES.iterator().next().name()));
        assertThat(item.getBoolean(User.NOTIFICATIONS_PROPERTY), is(NOTIFICATIONS_ENABLED));
    }

    @Test
    public void testCreateUserUsernameMustBeAlphanumeric() throws Exception {

    }

    @Test
    public void testCreateUserPasswordIsAtLeast6Characters() throws Exception {

    }

    @Test
    public void testMaximumUsernameLength() throws Exception {

    }

    @Test
    public void testMaximumFirstNameLength() throws Exception {

    }

    @Test
    public void testMaximumLastNameLength() throws Exception {

    }

    @Test
    public void testEmailFormat() throws Exception {
        
    }

    @Test
    public void testGetByIdWithNullValues() throws Exception {

    }

    @Test
    public void testCreateUserFailsForDuplicateUsername() throws Exception {

    }

    @Test
    public void testGetByUsernameWithNullValues() throws Exception {

    }
}