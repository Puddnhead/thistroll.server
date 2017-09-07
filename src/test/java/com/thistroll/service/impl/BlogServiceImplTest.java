package com.thistroll.service.impl;

import com.thistroll.data.api.BlogRepository;
import com.thistroll.domain.Blog;
import com.thistroll.service.client.dto.UpdateBlogRequest;
import com.thistroll.service.exceptions.BlogNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link BlogServiceImpl}
 *
 * Created by MVW on 9/7/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class BlogServiceImplTest {

    @InjectMocks
    private BlogServiceImpl blogService;

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private Blog mockBlog;

    private static final String BLOG_ID = "webelieve";

    @Test
    public void testGetMostRecentBlog() throws Exception {
        when(blogRepository.getMostRecentBlog()).thenReturn(mockBlog);
        Blog blog = blogService.getMostRecentBlog();
        assertThat(blog, is(mockBlog));
    }

    @Test(expected = BlogNotFoundException.class)
    public void testGetMostRecentBlogThrowsExceptionForUnknownBlog() throws Exception {
        blogService.getMostRecentBlog();
    }

    @Test
    public void testGetBlog() throws Exception {
        when(blogRepository.findById(BLOG_ID)).thenReturn(mockBlog);
        Blog blog = blogService.getBlog(BLOG_ID);
        assertThat(blog, is(mockBlog));
    }

    @Test(expected = BlogNotFoundException.class)
    public void testGetBlogByIdThrowsExceptionForUnknownBlog() throws Exception {
        blogService.getBlog(BLOG_ID);
    }

    @Test
    public void testUpdateBlog() throws Exception {
        UpdateBlogRequest request = new UpdateBlogRequest.Builder()
                .blogId(BLOG_ID)
                .build();
        when(blogRepository.findById(BLOG_ID)).thenReturn(mockBlog);
        when(blogRepository.update(request)).thenReturn(mockBlog);
        Blog blog = blogService.updateBlog(request);
        assertThat(blog, is(mockBlog));
    }

    @Test(expected = BlogNotFoundException.class)
    public void testUpdateBlogThrowsExceptionForUnknownBlog() throws Exception {
        UpdateBlogRequest request = new UpdateBlogRequest.Builder()
                .blogId(BLOG_ID)
                .build();
        blogService.updateBlog(request);
    }
}