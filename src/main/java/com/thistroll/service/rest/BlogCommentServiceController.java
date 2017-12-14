package com.thistroll.service.rest;

import com.thistroll.domain.BlogComment;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.BlogCommentService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rest layer for {@link com.thistroll.service.client.BlogService}
 *
 * Created by MVW on 12/13/2017.
 */
@Controller
@RequestMapping("/blog")
public class BlogCommentServiceController implements BlogCommentService {

    private BlogCommentService blogCommentService;

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    @Override
    public @ResponseBody BlogComment addComment(@RequestBody BlogComment blogComment) {
        return blogCommentService.addComment(blogComment);
    }

    @RequestMapping(value = "/{blogId}/comments", method = RequestMethod.GET)
    @Override
    public @ResponseBody List<BlogComment> getBlogCommentsForBlog(@PathVariable String blogId) {
        return blogCommentService.getBlogCommentsForBlog(blogId);
    }

    @RequestMapping(value = "/comment/delete", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public @ResponseBody Outcome deleteBlogComment(@RequestBody BlogComment blogComment) {
        return blogCommentService.deleteBlogComment(blogComment);
    }

    @Required
    public void setBlogCommentService(BlogCommentService blogCommentService) {
        this.blogCommentService = blogCommentService;
    }
}
