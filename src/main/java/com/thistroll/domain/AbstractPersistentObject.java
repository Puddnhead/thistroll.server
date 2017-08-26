package com.thistroll.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thistroll.domain.util.DateTimeSerializer;
import org.joda.time.DateTime;

import java.util.Objects;

/**
 * Abstract Persistent Class
 *
 * Uses an abstract builder, which is kind of a pain in the ass.
 *
 * Also uses the canEqual() style equals method, because sublcasses add state. This is also kind of a pain in the ass.
 */
public abstract class AbstractPersistentObject {

    public static final String CREATED_ON_PROPERTY = "createdOn";
    public static final String ID_PROPERTY = "id";
    public static final String LAST_UPDATED_ON_PROPERTY = "lastUpdatedOn";

    /**
     * Used as a primary key
     */
    protected final String id;

    /**
     * Used for sorting
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    protected final DateTime createdOn;

    @JsonSerialize(using = DateTimeSerializer.class)
    protected final DateTime lastUpdatedOn;

    /**
     * Private no-arg constructor for Jackson
     */
    protected AbstractPersistentObject() {
        this.id = null;
        this.createdOn = null;
        this.lastUpdatedOn = null;
    }

    protected AbstractPersistentObject(Builder builder) {
        this.id = builder.id;
        this.createdOn = builder.createdOn;
        this.lastUpdatedOn = builder.lastUpdatedOn;
    }

    public String getId() {
        return id;
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
        if (o == null || (!(o instanceof AbstractPersistentObject))) return false;
        AbstractPersistentObject that = (AbstractPersistentObject) o;
        return that.canEqual(this) &&
                Objects.equals(id, that.id) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(lastUpdatedOn, that.lastUpdatedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdOn, lastUpdatedOn);
    }

    public boolean canEqual(Object o) {
        return false;
    }

    public static abstract class Builder<T> {
        protected String id;
        protected DateTime createdOn;
        protected DateTime lastUpdatedOn;

        public T id(String id) {
            this.id = id;
            return (T)this;
        }

        public T createdOn(DateTime createdOn) {
            this.createdOn = createdOn;
            return (T)this;
        }

        public T lastUpdatedOn(DateTime lastUpdatedOn) {
            this.lastUpdatedOn = lastUpdatedOn;
            return (T)this;
        }
    }
}
