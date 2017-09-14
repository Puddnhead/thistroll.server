package com.thistroll.service.client;

import com.thistroll.domain.Blog;
import com.thistroll.domain.enums.Outcome;
import com.thistroll.service.client.dto.UpdateBlogRequest;

import java.util.List;
import java.util.Optional;

/**
 * Created by MVW on 7/11/2017.
 * Service for CRUD operations on blogs
 */
public interface BlogService {

    /**
     * Create a new blog. All provided values except for title and text will be ignored. The returned object contains
     * a generated ID.
     *
     * @param blog the blog object containing the title and text
     * @return the created blog including generated values for ID, createdOn, and lastUpdatedOn
     */
    Blog createBlog(Blog blog);

    /**
     * Fetches a blog by id
     *
     * @param id blog id
     * @return the blog
     * @throws com.thistroll.service.exceptions.BlogNotFoundException if the blog cannot be found
     */
    Blog getBlog(String id);

    /**
     * Return the most recent blog
     *
     * @return the most recent blog
     * @throws com.thistroll.service.exceptions.BlogNotFoundException if there are no blogs in the repository
     */
    Blog getMostRecentBlog();

    /**
     * Update a blog. Obeys empty "location" requests but ignores empty "title" and "text" fields.
     *
     * @param request the update blog request
     * @return the updated blog
     * @throws com.thistroll.service.exceptions.BlogNotFoundException if the blog cannot be found
     */
    Blog updateBlog(UpdateBlogRequest request);

    /**
     * Return a page of blogs ordered from most recent to oldest, including only id, title, and createdOn.
     * pageNumber and pageSize are optional parameters
     *
     * @param pageNumber optional ZERO-BASED page number. default is 0
     * @param pageSize optional page size. default is a configurable property
     * @return all blogs ordered from most recent to oldest, including only id, title, and createdOn
     */
    List<Blog> getBlogs(Optional<Integer> pageNumber, Optional<Integer> pageSize);

    /**
     * Delete a blog
     *
     * @param blogId the blog id
     * @return success if the blog could be found else failure
     */
    Outcome deleteBlog(String blogId);
}
