package com.thistroll.service.impl;

import com.thistroll.data.api.BlogRepository;
import com.thistroll.domain.Blog;
import com.thistroll.service.client.BlogService;
import com.thistroll.service.exceptions.BlogNotFoundException;
import org.springframework.beans.factory.annotation.Required;

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

    @Override
    public Blog createBlog(Blog blog) {
        return blogRepository.create(blog);
    }

    @Override
    public Blog getBlog(String id) {
        Blog blog = blogRepository.findById(id);
        if (blog == null) {
            throw new BlogNotFoundException("No blog with id " + id);
        }
        return blog;
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
