package com.thistroll.service.client.dto;

import com.thistroll.service.client.dto.request.UpdateUserRequest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by MVW on 8/31/2017.
 */
public class UpdateUserRequestTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(UpdateUserRequest.class).verify();
    }

}