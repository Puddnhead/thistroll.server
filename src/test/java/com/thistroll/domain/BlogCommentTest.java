package com.thistroll.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for {@link BlogComment}
 *
 * Created by MVW on 12/13/2017.
 */
public class BlogCommentTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(BlogComment.class).withRedefinedSuperclass();
    }

}