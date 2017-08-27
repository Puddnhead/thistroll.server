package com.thistroll.service.client;

import com.thistroll.domain.Blog;

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
    public Blog createBlog(Blog blog);

    /**
     * Fetches a blog by id
     *
     * @param id blog id
     * @return the blog
     */
    public Blog getBlog(String id);

    /**
     * Return the most recent blog
     *
     * @return the most recent blog
     */
    public Blog getMostRecentBlog();


    /**
     * Return all blogs ordered from most recent to oldest, including only id, title, and createdOn
     *
     * @return all blogs ordered from most recent to oldest, including only id, title, and createdOn
     */
    public List<Blog> getAllBlogs();
}
