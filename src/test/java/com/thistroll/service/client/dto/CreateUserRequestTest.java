package com.thistroll.service.client.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MVW on 8/26/2017.
 */
public class CreateUserRequestTest {

    @Test
    public void testEqualsHashcode() throws Exception {
        EqualsVerifier.forClass(CreateUserRequest.class).verify();
    }
}