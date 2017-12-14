package com.thistroll.service.impl;

import com.thistroll.data.api.BlogCommentRepository;
import com.thistroll.domain.BlogComment;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.server.RequestValues;
import com.thistroll.server.logging.ThrowsError;
import com.thistroll.service.client.BlogCommentService;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Implementation class for {@link com.thistroll.service.client.BlogCommentService}
 *
 * Created by MVW on 12/13/2017.
 */
public class BlogCommentServiceImpl implements BlogCommentService {

    private BlogCommentRepository blogCommentRepository;

    @PreAuthorize("isLoggedIn()")
    @ThrowsError
    @Override
    public BlogComment addComment(BlogComment blogComment) {
        String username = RequestValues.getSession().getUserDetails().getUsername();
        String sanitizedComment = Jsoup.clean(blogComment.getComment(), Whitelist.simpleText());
        BlogComment commentWithCurrentUser = new BlogComment.Builder()
                .comment(sanitizedComment)
                .blogId(blogComment.getBlogId())
                .username(username)
                .build();

        return blogCommentRepository.addCommentToBlog(commentWithCurrentUser);
    }

    @ThrowsError
    @Override
    public List<BlogComment> getBlogCommentsForBlog(String blogId) {
        return blogCommentRepository.getCommentsForBlog(blogId);
    }

    @PreAuthorize("isAdmin()")
    @ThrowsError
    @Override
    public Outcome deleteBlogComment(BlogComment blogComment) {
        return blogCommentRepository.deleteBlogComment(blogComment);
    }

    @Required
    public void setBlogCommentRepository(BlogCommentRepository blogCommentRepository) {
        this.blogCommentRepository = blogCommentRepository;
    }
}
