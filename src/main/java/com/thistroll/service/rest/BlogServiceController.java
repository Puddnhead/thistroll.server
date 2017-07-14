package com.thistroll.service.rest;

import com.thistroll.domain.Blog;
import com.thistroll.service.client.BlogService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by MVW on 7/11/2017.
 * Rest layer for {@link com.thistroll.service.client.BlogService}
 */
@Controller
@RequestMapping("/")
public class BlogServiceController implements BlogService {

    private BlogService blogService;

    @RequestMapping(value = "blog/current", method = RequestMethod.GET)
    @Override
    public @ResponseBody Blog getMostRecentBlog() {
        return blogService.getMostRecentBlog();
    }

    @RequestMapping(value = "blog", method = RequestMethod.POST)
    @Override
    public @ResponseBody Blog createBlog(@RequestBody Blog blog) {
        return blogService.createBlog(blog);
    }

    @RequestMapping(value = "blog", method = RequestMethod.GET)
    @Override
    public @ResponseBody Blog getBlog(@RequestParam String id) {
        return blogService.getBlog(id);
    }

    @Required
    public void setBlogService(BlogService blogService) {
        this.blogService = blogService;
    }
}
