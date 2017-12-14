package com.thistroll.service.client;

import com.thistroll.domain.BlogComment;
import com.thistroll.domain.enums.Outcome;

import java.util.List;

/**
 * Service for fetching comments for blogs
 *
 * Created by MVW on 12/13/2017.
 */
public interface BlogCommentService {

    /**
     * Add the blog comment with the provided comment and blogId
     * Sanitizes the input to remove all tags except simple text (b, em, i, strong, u)
     *
     * @param blogComment a blog comment containing a blog id and a textual comment less than 512 characters
     * @return the blog comment with generated ids and dates if successful
     */
    BlogComment addComment(BlogComment blogComment);

    /**
     * Fetch all comments for the given blog
     *
     * @param blogId blog id
     * @return all comments for the given blog
     */
    List<BlogComment> getBlogCommentsForBlog(String blogId);

    /**
     * Delete a blog comment with the given blog comment id and blog id
     *
     * @param blogComment the blog comment
     * @return success if the item was successfully deleted
     */
    Outcome deleteBlogComment(BlogComment blogComment);
}
