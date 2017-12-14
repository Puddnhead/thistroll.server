package com.thistroll.domain;

import java.util.Objects;

/**
 * Domain representation of a comment on a blog. These are partitioned by blogId and ordered by createdOn. There is a
 * many to one relationship between Blogs and BlogComments.
 *
 * Created by MVW on 12/13/2017.
 */
public class BlogComment extends AbstractPersistentObject {

    public static final String CREATED_ON_INDEX = "createdOnIndex";

    public static final String COMMENT_PROPERTY = "comment";
    public static final String BLOG_ID_PROPERTY = "blogId";
    public static final String USERNAME_PROPERTY = "username";

    private final String comment;

    private final String blogId;

    private final String username;

    /**
     * No-arg constructor required by Jackson
     */
    public BlogComment() {
        super();
        this.comment = null;
        this.blogId = null;
        this.username = null;
    }

    private BlogComment(Builder builder) {
        super(builder);
        this.comment = builder.comment;
        this.blogId = builder.blogId;
        this.username = builder.username;
    }

    public String getComment() {
        return comment;
    }

    public String getBlogId() {
        return blogId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlogComment)) return false;
        if (!super.equals(o)) return false;
        BlogComment that = (BlogComment) o;
        return that.canEqual(this) &&
                Objects.equals(comment, that.comment) &&
                Objects.equals(blogId, that.blogId) &&
                Objects.equals(username, that.username);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(super.hashCode(), comment, blogId, username);
    }

    public final boolean canEqual(Object o) {
        return o instanceof BlogComment;
    }

    public static final class Builder extends AbstractPersistentObject.Builder<Builder> {
        private String comment;
        private String blogId;
        private String username;

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder blogId(String blogId) {
            this.blogId = blogId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public BlogComment build() {
            return new BlogComment(this);
        }
    }
}
