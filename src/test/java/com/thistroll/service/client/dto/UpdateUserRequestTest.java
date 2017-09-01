package com.thistroll.service.client.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MVW on 8/31/2017.
 */
public class UpdateUserRequestTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(UpdateUserRequest.class).verify();
    }

}