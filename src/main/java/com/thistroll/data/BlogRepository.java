package com.thistroll.data;

import com.thistroll.domain.Blog;

import java.util.List;

/**
 * API for crud operations on {@link com.thistroll.domain.Blog} instances
 *
 * Created by MVW on 7/13/2017.
 */
public interface BlogRepository {

    /**
     * Saves the given blog. Returns the created instance with a generated ID and generated createdOn and
     * lastUpdatedOn values. Ignores any provided ID, createdOn, and lastUpdatedOn values.
     *
     * @param blog the new blog
     * @return the created blog with an auto-generated id
     */
    Blog create(Blog blog);

    /**
     * Updates the given blog. Returns the updated instance with a new lastUpdatedOn value. Ignores the provided
     * lastUpdated on value
     *
     * @param blog the blog to update
     * @return the updated blog with a new lastUpdatedOn value
     */
    Blog update(Blog blog);

    /**
     * Returns the blog with the given id
     *
     * @param id the blog id
     * @return the blog with the given id
     */
    Blog findById(String id);

    /**
     * Returns the blog with the most recent createdOn date
     *
     * @return the blog with most recent createdOn date
     */
    Blog getMostRecentBlog();

    /**
     * Deletes the blog with the provided id
     *
     * @param id blog id
     * @throws com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException if no blog with the given id exists
     */
    void deleteBlog(String id);

    /**
     * Returns a subset of all blogs determined by pageNumber (1-based) and pageSize, ordered by createdOn date
     *
     * @param pageNumber a 1-based page number
     * @param pageSize the number of blogs per page
     * @return a subset of all blogs determined by page number and size
     */
    List<Blog> getPageableBlogList(int pageNumber, int pageSize);
}
