package com.thistroll.service.client.dto.request;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for {@link RegisterUserRequest}
 *
 * Created by MVW on 9/14/2017.
 */
public class RegisterUserRequestTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(RegisterUserRequest.class).verify();
    }
}