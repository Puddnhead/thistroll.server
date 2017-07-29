package com.thistroll.service.client;

import java.util.List;

/**
 * Created by MVW on 7/29/2017.
 */
public interface BlogImagesService {

    /**
     * Return a list of image URLs associated with the blog
     *
     * @param blogId blog id
     * @return a list of URL links to images associated with the blog
     */
    public List<String> getImages(String blogId);
}
