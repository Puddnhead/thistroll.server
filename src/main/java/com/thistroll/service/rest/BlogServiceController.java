package com.thistroll.service.rest;

import com.thistroll.service.client.BlogService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by MVW on 7/11/2017.
 * Rest layer for {@link com.thistroll.service.client.BlogService}
 */
@Controller
@RequestMapping("/")
public class BlogServiceController implements BlogService {

    private BlogService blogService;

    @RequestMapping(value = "blog/current", method = RequestMethod.GET)
    public @ResponseBody String getMostRecentBlog() {
        return blogService.getMostRecentBlog();
    }

    @Required
    public void setBlogService(BlogService blogService) {
        this.blogService = blogService;
    }
}
