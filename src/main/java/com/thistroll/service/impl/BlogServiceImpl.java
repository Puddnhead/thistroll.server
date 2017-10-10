package com.thistroll.service.impl;

import com.thistroll.data.api.BlogRepository;
import com.thistroll.domain.Blog;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.exceptions.BlogNotFoundException;
import com.thistroll.server.logging.ThrowsError;
import com.thistroll.server.logging.ThrowsWarning;
import com.thistroll.service.client.BlogService;
import com.thistroll.service.client.dto.request.UpdateBlogRequest;
import com.thistroll.service.client.dto.response.GetBlogsResponse;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Created by micha on 7/11/2017.
 * Implementation for {@link com.thistroll.service.client.BlogService}
 */
public class BlogServiceImpl implements BlogService {

    private BlogRepository blogRepository;

    private int defaultPageSize;

    @ThrowsWarning
    @Override
    public Blog getMostRecentBlog() {
        Blog blog = blogRepository.getMostRecentBlog();
        if (blog == null) {
            throw new BlogNotFoundException("No blogs available");
        }
        return blog;
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public Blog createBlog(Blog blog) {
        return blogRepository.create(blog);
    }

    @Override
    @ThrowsWarning
    public Blog getBlog(String id) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new BlogNotFoundException("Could not find blog with id " + id);
        }
        return blog;
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public Blog updateBlog(UpdateBlogRequest request) {
        Blog oldBlog = getBlog(request.getBlogId());
        if (oldBlog == null) {
            throw new BlogNotFoundException("Could not find a blog with id " + request.getBlogId());
        }

        return blogRepository.update(request);
    }

    @ThrowsError
    @Override
    public GetBlogsResponse getBlogs(Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        return blogRepository.getPageableBlogList(pageNumber.orElse(0), pageSize.orElse(defaultPageSize));
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public Outcome deleteBlog(String blogId) {
        return blogRepository.deleteBlog(blogId);
    }

    @Required
    public void setBlogRepository(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Required
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
}
