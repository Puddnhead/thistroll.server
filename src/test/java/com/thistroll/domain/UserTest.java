package com.thistroll.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by MVW on 8/22/2017.
 */
public class UserTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(User.class).withRedefinedSuperclass().verify();
    }

}