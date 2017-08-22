package com.thistroll.domain;

import org.joda.time.DateTime;

/**
 * Abstract Builder class
 */
public abstract class AbstractPersistentObjectBuilder<T extends AbstractPersistentObjectBuilder> {
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
