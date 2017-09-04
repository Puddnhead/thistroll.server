package com.thistroll.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MVW on 8/31/2017.
 */
public class SessionTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(Session.class).withRedefinedSuperclass().verify();
    }
}