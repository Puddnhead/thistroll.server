package com.thistroll.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Unit test for {@link Blog}
 *
 * Created by micha on 7/13/2017.
 */
public class BlogTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(Blog.class).withRedefinedSuperclass().verify();
    }
    
}