package com.thistroll.service.rest;

import com.thistroll.data.api.BlogCommentRepository;
import com.thistroll.domain.BlogComment;
import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.server.RequestValues;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * REST test for {@link BlogCommentServiceController}
 *
 * Created by MVW on 12/13/2017.
 */
public class BlogCommentServiceControllerTest extends ControllerTestBase {

    @Autowired
    private BlogCommentRepository repository;

    private static final String BLOG_ID = "abc";
    private static final String COMMENT = "I love the blog";
    private static final String USERNAME = "username";
    private static final String ID = "def";

    private Session mockSession = mock(Session.class);
    private User mockUserDetails = mock(User.class);

    @Before
    public void setup() throws Exception {
        when(mockSession.getUserDetails()).thenReturn(mockUserDetails);
        when(mockUserDetails.getUsername()).thenReturn(USERNAME);
        RequestValues.setSession(mockSession);
    }

    @After
    public void tearDown() throws Exception {
        RequestValues.setSession(null);
    }

    @Test
    public void testAddComment() throws Exception {
        BlogComment blogComment = createBlogComment();
        when(repository.addCommentToBlog(any(BlogComment.class))).thenReturn(blogComment);

        String serializedRequest = objectMapper.writeValueAsString(blogComment);
        MvcResult mvcResult = mockMvc.perform(post("/blog/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedRequest))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody.contains(ID), is(true));
    }

    @Test
    public void testGetCommentsForBlog() throws Exception {
        mockMvc.perform(get("/blog/abc123/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteComment() throws Exception {
        BlogComment blogComment = createBlogComment();

        String serializedRequest = objectMapper.writeValueAsString(blogComment);
        mockMvc.perform(post("/blog/comment/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedRequest))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private BlogComment createBlogComment() throws Exception {
        return new BlogComment.Builder()
                .id(ID)
                .blogId(BLOG_ID)
                .comment(COMMENT)
                .username(USERNAME)
                .build();
    }
}