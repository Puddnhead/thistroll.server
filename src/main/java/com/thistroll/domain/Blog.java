package com.thistroll.domain;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Created by MVW on 7/13/2017.
 */
public class Blog {

    /**
     * A GUID
     */
    private String id;

    private String title;

    /**
     * Blog text - encoded HTML (@apos; and the like)
     */
    private String text;

    private DateTime createdOn;

    private DateTime lastUpdatedOn;

    /**
     * Private constructor to force use of Builder
     */
    private Blog(String id, String title, String text, DateTime createdOn, DateTime lastUpdatedOn) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }

    public DateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blog blog = (Blog) o;
        return Objects.equals(title, blog.title) &&
                Objects.equals(id, blog.id) &&
                Objects.equals(text, blog.text) &&
                Objects.equals(createdOn, blog.createdOn) &&
                Objects.equals(lastUpdatedOn, blog.lastUpdatedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, text, createdOn, lastUpdatedOn);
    }

    /**
     * Builder class
     */
    public static final class Builder {
        private String id;
        private String title;
        private String text;
        private DateTime createdOn;
        private DateTime lastUpdatedOn;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder createdOn(DateTime createdOn) {
            this.createdOn = createdOn;
            return this;
        }

        public Builder lastUpdatedOn(DateTime lastUpdatedOn) {
            this.lastUpdatedOn = lastUpdatedOn;
            return this;
        }

        private void validate() {
            notEmpty(title);
        }

        public Blog build() {
            validate();
            return new Blog(id, title, text, createdOn, lastUpdatedOn);
        }
    }
}
