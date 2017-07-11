package com.thistroll.service.client;

/**
 * Created by MVW on 7/11/2017.
 * Service for fetching blogs
 */
public interface BlogService {

    /**
     * TODO: Use a Blog Pojo instead of a string return type
     * Return the most recent blog
     *
     * @return the most recent blog
     */
    public String getMostRecentBlog();
}
