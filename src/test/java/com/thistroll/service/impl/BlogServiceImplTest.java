package com.thistroll.service.impl;

import com.thistroll.data.api.BlogRepository;
import com.thistroll.domain.Blog;
import com.thistroll.service.client.dto.request.UpdateBlogRequest;
import com.thistroll.service.client.dto.response.GetBlogsResponse;
import com.thistroll.service.exceptions.BlogNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
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

    @Captor
    private ArgumentCaptor<Integer> pageNumberCaptor;

    @Captor
    private ArgumentCaptor<Integer> pageSizeCaptor;

    private static final String BLOG_ID = "webelieve";
    private static final int PAGE_SIZE = 8;

    @Before
    public void setup() {
        blogService.setDefaultPageSize(PAGE_SIZE);
    }

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

    @Test
    public void testGetBlogsDefaultPageAndSize() throws Exception {
        when(blogRepository.getPageableBlogList(pageNumberCaptor.capture(), pageSizeCaptor.capture()))
                .thenReturn(new GetBlogsResponse(Collections.singletonList(mockBlog), true));
        List<Blog> blogs = blogService.getBlogs(Optional.empty(), Optional.empty()).getBlogs();
        assertThat(blogs.size(), is(1));
        assertThat(blogs.get(0), is(mockBlog));
        assertThat(pageNumberCaptor.getValue(), is(0));
        assertThat(pageSizeCaptor.getValue(), is(PAGE_SIZE));
    }

    @Test
    public void testGetBlogsProvidedPageAndSize() throws Exception {
        when(blogRepository.getPageableBlogList(pageNumberCaptor.capture(), pageSizeCaptor.capture()))
                .thenReturn(new GetBlogsResponse(Collections.singletonList(mockBlog), true));
        List<Blog> blogs = blogService.getBlogs(Optional.of(2), Optional.of(10)).getBlogs();
        assertThat(blogs.size(), is(1));
        assertThat(blogs.get(0), is(mockBlog));
        assertThat(pageNumberCaptor.getValue(), is(2));
        assertThat(pageSizeCaptor.getValue(), is(10));
    }
}