package com.thistroll.service.client.dto;

import com.thistroll.service.client.dto.request.CreateUserRequest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by MVW on 8/26/2017.
 */
public class CreateUserRequestTest {

    @Test
    public void testEqualsHashcode() throws Exception {
        EqualsVerifier.forClass(CreateUserRequest.class).verify();
    }
}