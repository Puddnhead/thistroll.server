package com.thistroll.domain;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit test for {@link Blog}
 *
 * Created by micha on 7/13/2017.
 */
public class BlogTest {

    @Test
    public void testEquals() throws Exception {
        Blog blog1 = new Blog.Builder()
                .id("id1")
                .title("title1")
                .location("antarctica")
                .text("text1")
                .build();

        Blog blog2 = new Blog.Builder()
                .id("id1")
                .title("title1")
                .location("antarctica")
                .text("text1")
                .createdOn(blog1.getCreatedOn())
                .lastUpdatedOn(blog1.getLastUpdatedOn())
                .build();

        Blog blog3 = new Blog.Builder()
                .id("id2")
                .title("title1")
                .location("antarctica")
                .text("text1")
                .build();

        assertThat(blog1, is(blog2));
        assertThat(blog1, is(not(blog3)));
    }

    @Test(expected = NullPointerException.class)
    public void testValidation() throws Exception {
        new Blog.Builder()
                .text("someText")
                .build();
    }
}