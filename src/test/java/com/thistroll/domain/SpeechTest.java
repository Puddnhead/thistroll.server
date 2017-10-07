package com.thistroll.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Unit test for {@link Speech}
 *
 * Created by MVW on 10/6/2017.
 */
public class SpeechTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(Speech.class).withRedefinedSuperclass().verify();
    }

    @Test
    public void testGetRandomResponse() throws Exception {
        final String response1 = "one";
        final String response2 = "two";
        Speech speech = new Speech.Builder()
                .id("id")
                .text("blah")
                .responses(Arrays.asList(response1, response2))
                .build();

        boolean found1 = false, found2 = false;
        for (int i = 0; i < 1000; i++) {
            String response = speech.getRandomResponse();
            if (response.equals(response1)) {
                found1 = true;
            }
            if (response.equals(response2)) {
                found2 = true;
            }
        }

        assertThat(found1, is(true));
        assertThat(found2, is(true));
    }
}