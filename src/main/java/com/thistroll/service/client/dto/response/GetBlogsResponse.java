package com.thistroll.service.client.dto.response;

import com.thistroll.domain.Blog;

import java.util.List;
import java.util.Objects;

/**
 * Response DTO for a get blogs request
 *
 * Created by MVW on 9/14/2017.
 */
public class GetBlogsResponse {

    private final List<Blog> blogs;

    private final boolean moreBlogs;

    /**
     * No-arg constructor required by Jackson
     */
    public GetBlogsResponse() {
        this.blogs = null;
        this.moreBlogs = false;
    }

    public GetBlogsResponse(List<Blog> blogs, boolean moreBlogs) {
        this.blogs = blogs;
        this.moreBlogs = moreBlogs;
    }

    public List<Blog> getBlogs() {
        return blogs;
    }

    public boolean isMoreBlogs() {
        return moreBlogs;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof GetBlogsResponse)) return false;
        GetBlogsResponse that = (GetBlogsResponse) o;
        return moreBlogs == that.moreBlogs &&
                Objects.equals(blogs, that.blogs);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(blogs, moreBlogs);
    }
}
