package com.thistroll.service.client;

import com.thistroll.domain.Blog;
import com.thistroll.service.client.dto.UpdateBlogRequest;

import java.util.List;

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
     */
    Blog getBlog(String id);

    /**
     * Return the most recent blog
     *
     * @return the most recent blog
     */
    Blog getMostRecentBlog();

    /**
     * Update a blog. Obeys empty "location" requests but ignores empty "title" and "text" fields.
     *
     * @param request the update blog request
     * @return the updated blog
     */
    Blog updateBlog(UpdateBlogRequest request);

    /**
     * Return all blogs ordered from most recent to oldest, including only id, title, and createdOn
     *
     * @return all blogs ordered from most recent to oldest, including only id, title, and createdOn
     */
    List<Blog> getAllBlogs();
}
