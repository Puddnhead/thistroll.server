package com.thistroll.service.impl;

import com.thistroll.data.api.BlogRepository;
import com.thistroll.domain.Blog;
import com.thistroll.service.client.BlogService;
import com.thistroll.service.client.dto.UpdateBlogRequest;
import com.thistroll.service.exceptions.BlogNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by micha on 7/11/2017.
 * Implementation for {@link com.thistroll.service.client.BlogService}
 */
public class BlogServiceImpl implements BlogService {

    private BlogRepository blogRepository;

    @Override
    public Blog getMostRecentBlog() {
        Blog blog = blogRepository.getMostRecentBlog();
        if (blog == null) {
            throw new BlogNotFoundException("No blogs available");
        }
        return blog;
    }

    @PreAuthorize("isAdmin()")
    @Override
    public Blog createBlog(Blog blog) {
        return blogRepository.create(blog);
    }

    @Override
    public Blog getBlog(String id) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new BlogNotFoundException("Could not find blog with id " + id);
        }
        return blog;
    }

    @PreAuthorize("isAdmin()")
    @Override
    public Blog updateBlog(UpdateBlogRequest request) {
        Blog oldBlog = getBlog(request.getBlogId());
        if (oldBlog == null) {
            throw new BlogNotFoundException("Could not find a blog with id " + request.getBlogId());
        }

        return blogRepository.update(request);
    }

    @Override
    public List<Blog> getAllBlogs() {
        return blogRepository.getAllBlogs();
    }

    @Required
    public void setBlogRepository(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }
}
