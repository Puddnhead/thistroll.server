package com.thistroll.data.api;

import com.thistroll.domain.BlogComment;
import com.thistroll.domain.enums.Outcome;

import java.util.List;

/**
 * Repository for fetching Blog Comments
 *
 * Created by MVW on 12/13/2017.
 */
public interface BlogCommentRepository {

    /**
     * Adds a comment to a blog, generating id, createdOn, and lastUpdated on properties
     *
     * @param blogComment the blog comment
     * @return the blog comment with generated fields
     */
    BlogComment addCommentToBlog(BlogComment blogComment);

    /**
     * Get all blog comments for a blog, ordered from oldest to most recent.
     *
     * @param blogId the blog id
     * @return a list of all comments for the blog, ordered from oldest to most recent
     */
    List<BlogComment> getCommentsForBlog(String blogId);

    /**
     * Delete the provided blogComment - blogId and id are required fields in the entity
     *
     * @return success if the comment was deleted else failure
     */
    Outcome deleteBlogComment(BlogComment blogComment);
}
