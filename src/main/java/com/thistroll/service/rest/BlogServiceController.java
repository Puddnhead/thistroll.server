package com.thistroll.service.rest;

import com.thistroll.domain.Blog;
import com.thistroll.service.client.BlogService;
import com.thistroll.service.client.dto.UpdateBlogRequest;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by MVW on 7/11/2017.
 * Rest layer for {@link com.thistroll.service.client.BlogService}
 */
@Controller
@RequestMapping("/blog")
public class BlogServiceController implements BlogService {

    private BlogService blogService;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @Override
    public @ResponseBody Blog getMostRecentBlog() {
        return blogService.getMostRecentBlog();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Override
    public @ResponseBody Blog createBlog(@RequestBody Blog blog) {
        return blogService.createBlog(blog);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public @ResponseBody Blog getBlog(@PathVariable String id) {
        return blogService.getBlog(id);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @Override
    public @ResponseBody Blog updateBlog(@RequestBody UpdateBlogRequest request) {
        return blogService.updateBlog(request);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @Override
    public @ResponseBody List<Blog> getAllBlogs() {
        return blogService.getAllBlogs();
    }

    @Required
    public void setBlogService(BlogService blogService) {
        this.blogService = blogService;
    }
}
