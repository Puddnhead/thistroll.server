package com.thistroll.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thistroll.domain.util.DateTimeSerializer;
import org.joda.time.DateTime;

import java.util.Objects;

/**
 * Abstract Persistent Class
 *
 * Inheriting class is responsible for setting inherited fields during construction. Because AbstractBuilder inheritance
 * is a pain in the ass.
 */
public abstract class AbstractPersistentObject {
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

    protected AbstractPersistentObject(AbstractPersistentObjectBuilder builder) {
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
}
