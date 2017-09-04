package com.thistroll.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thistroll.domain.util.DateTimeSerializer;
import org.joda.time.DateTime;

import java.util.Objects;

/**
 * Domain representation of a user session
 *
 * Created by MVW on 8/31/2017.
 */
public class Session extends AbstractPersistentObject {

    private final User userDetails;

    @JsonSerialize(using = DateTimeSerializer.class)
    private final DateTime expirationTime;

    /**
     * No-arg constructor required by Jackson
     */
    public Session() {
        super();
        this.userDetails = null;
        this.expirationTime = null;
    }

    private Session(Builder builder) {
        super(builder);
        this.userDetails = builder.userDetails;
        this.expirationTime = builder.expirationTime;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Session)) return false;
        if (!super.equals(o)) return false;
        Session session = (Session) o;
        return session.canEqual(this) &&
                Objects.equals(userDetails, session.userDetails) &&
                Objects.equals(expirationTime, session.expirationTime);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(super.hashCode(), userDetails, expirationTime);
    }

    @Override
    public boolean canEqual(Object o) {
        return o instanceof Session;
    }

    public User getUserDetails() {
        return userDetails;
    }

    public DateTime getExpirationTime() {
        return expirationTime;
    }

    public static final class Builder extends AbstractPersistentObject.Builder<Builder> {
        private User userDetails;
        private DateTime expirationTime;

        public Builder userDetails(User userDetails) {
            this.userDetails = userDetails;
            return this;
        }

        public Builder expirationTime(DateTime expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        public Session build() {
            return new Session(this);
        }
    }
}
