package com.thistroll.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by MVW on 8/22/2017.
 */
public class UserTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(User.class).withRedefinedSuperclass().verify();
    }

    @Test(expected = NullPointerException.class)
    public void testValidationEmptyUsername() throws Exception {
        new User.Builder()
                .email("email")
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void testValidationEmptyEmail() throws Exception {
        new User.Builder()
                .username("username")
                .build();
    }

    @Test
    public void testValidationRequiredFieldsPresent() throws Exception {
        User user = new User.Builder()
                .username("Joe")
                .email("joe@joe.com")
                .build();
        assertThat(user == null, is(false));
    }
}