package com.thistroll.service.impl;

import com.thistroll.data.BlogRepository;
import com.thistroll.domain.Blog;
import com.thistroll.service.client.BlogService;
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
        return blogRepository.getMostRecentBlog();
    }

    @Override
    public Blog createBlog(Blog blog) {
        return blogRepository.create(blog);
    }

    @Override
    public Blog getBlog(String id) {
        return blogRepository.findById(id);
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
