package com.thistroll.service.impl;

import com.thistroll.data.api.BlogCommentRepository;
import com.thistroll.domain.BlogComment;
import com.thistroll.domain.Session;
import com.thistroll.domain.User;
import com.thistroll.server.RequestValues;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link BlogCommentServiceImpl}
 *
 * Created by MVW on 12/13/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class BlogCommentServiceImplTest {

    @InjectMocks
    private BlogCommentServiceImpl blogCommentService;

    @Mock
    private BlogCommentRepository blogCommentRepository;

    @Mock
    private Session mockSession;

    @Mock
    private User mockUserDetails;

    @Captor
    private ArgumentCaptor<BlogComment> blogCommentCaptor;

    private static final String USERNAME = "francesca";
    private static final String BLOG_ID = "blogId";
    private static final String COMMENT = "ready to be done testing";

    @Before
    public void setup() throws Exception {
        when(mockSession.getUserDetails()).thenReturn(mockUserDetails);
        when(mockUserDetails.getUsername()).thenReturn(USERNAME);
        when(blogCommentRepository.addCommentToBlog(blogCommentCaptor.capture())).thenReturn(mock(BlogComment.class));
        RequestValues.setSession(mockSession);
    }

    @After
    public void tearDown() throws Exception {
        RequestValues.setSession(null);
    }

    @Test
    public void testInjectsUsernameFromSession() throws Exception {
        BlogComment original = new BlogComment.Builder()
                .username("ignored user name")
                .blogId(BLOG_ID)
                .comment(COMMENT)
                .build();
        blogCommentService.addComment(original);

        BlogComment captured = blogCommentCaptor.getValue();
        assertThat(captured.getUsername(), is(USERNAME));
    }

    @Test
    public void testCommentSanitization() throws Exception {
        BlogComment original = new BlogComment.Builder()
                .blogId(BLOG_ID)
                .comment("I <a href='http://malicious.com'>hack</a> <script>alert('blah')</script>")
                .build();
        blogCommentService.addComment(original);

        BlogComment captured = blogCommentCaptor.getValue();
        assertThat(captured.getComment(), is("I hack"));
    }
}