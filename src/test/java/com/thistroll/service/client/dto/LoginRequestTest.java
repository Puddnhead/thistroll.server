package com.thistroll.service.client.dto;

import com.thistroll.service.client.dto.request.LoginRequest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Unit test for {@link LoginRequest}
 *
 * Created by MVW on 9/6/2017.
 */
public class LoginRequestTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(LoginRequest.class);
    }

}