package com.thistroll.service.client.dto.response;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for {@link GetBlogsResponse}
 *
 * Created by MVW on 9/14/2017.
 */
public class GetBlogsResponseTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(GetBlogsResponse.class).verify();
    }
}