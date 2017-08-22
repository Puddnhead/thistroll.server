package com.thistroll.domain;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notEmpty;

/**
 * Created by MVW on 7/13/2017.
 */
public class Blog extends AbstractPersistentObject {

    /**
     * No partitions as this table will never be huge and this makes querying for recent blogs easier
     */
    public static final String PARTITION_KEY_NAME = "PARTITION_KEY";
    public static final String PARTITION_KEY_VALUE = "NO_PARTITIONS";

    private final String location;

    private final String title;

    /**
     * Blog text - encoded HTML (@apos; and the like)
     */
    private final String text;

    public static final String CREATED_ON_PROPERTY = "createdOn";
    public static final String ID_PROPERTY = "id";
    public static final String LOCATION_PROPERTY = "location";
    public static final String TITLE_PROPERTY = "title";
    public static final String TEXT_PROPERTY = "text";
    public static final String LAST_UPDATED_ON_PROPERTY = "lastUpdatedOn";

    public static final String CREATED_ON_INDEX = "createdOnIndex";

    /**
     * Private no-arg constructor for Jackson
     */
    private Blog() {
        super();
        this.title = null;
        this.location = null;
        this.text = null;
    }

    /**
     * Private constructor to force use of Builder
     */
    private Blog(Builder builder) {
        super(builder);
        this.title = builder.title;
        this.location = builder.location;
        this.text = builder.text;
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
        if (o == null || !(o instanceof Blog)) return false;
        if (!super.equals(o)) return false;
        Blog blog = (Blog) o;
        return blog.canEqual(this) &&
                Objects.equals(title, blog.title) &&
                Objects.equals(location, blog.location) &&
                Objects.equals(text, blog.text);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(title, location, text, super.hashCode());
    }

    public boolean canEqual(Object o) {
        return o instanceof Blog;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", text='" + text + '\'' +
                ", createdOn=" + createdOn +
                ", lastUpdatedOn=" + lastUpdatedOn +
                '}';
    }

    /**
     * Builder class
     */
    public static final class Builder extends AbstractPersistentObject.Builder<Builder> {
        private String title;
        private String location;
        private String text;

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

        private void validate() {
            notEmpty(title);
        }

        public Blog build() {
            validate();
            return new Blog(this);
        }
    }
}
