package com.thistroll.service.client.dto.request;

import java.util.Objects;

/**
 * A DTO for updating blogs
 *
 * Created by MVW on 9/7/2017.
 */
public class UpdateBlogRequest {

    private String id;

    private String title;

    private String location;

    private String text;

    /**
     * No-arg constructor for Jackson
     */
    private UpdateBlogRequest() {
        this.id = null;
        this.title = null;
        this.location = null;
        this.text = null;
    }

    private UpdateBlogRequest(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.location = builder.location;
        this.text = builder.text;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getText() {
        return text;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof UpdateBlogRequest)) return false;
        UpdateBlogRequest that = (UpdateBlogRequest) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(location, that.location) &&
                Objects.equals(text, that.text);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id, title, location, text);
    }

    public static final class Builder {
        private String id;
        private String title;
        private String location;
        private String text;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public UpdateBlogRequest build() {
            return new UpdateBlogRequest(this);
        }
    }
}
