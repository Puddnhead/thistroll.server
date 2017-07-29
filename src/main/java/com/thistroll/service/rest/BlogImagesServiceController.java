package com.thistroll.service.rest;

import com.thistroll.service.client.BlogImagesService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by MVW on 7/29/2017.
 */
@Controller
@RequestMapping("/")
public class BlogImagesServiceController implements BlogImagesService {

    private BlogImagesService blogImagesService;

    @RequestMapping(value = "blog/images", method = RequestMethod.GET)
    @Override
    public @ResponseBody List<String> getImages(@RequestParam String blogId) {
        return blogImagesService.getImages(blogId);
    }

    @Required
    public void setBlogImagesService(BlogImagesService blogImagesService) {
        this.blogImagesService = blogImagesService;
    }
}
