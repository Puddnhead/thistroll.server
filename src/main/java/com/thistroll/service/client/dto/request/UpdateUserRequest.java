package com.thistroll.service.client.dto.request;

import java.util.Objects;

/**
 * A DTO for requesting updates to user objects.
 *
 * If a userId is provided, it will be used to locate the user. Otherwise a username must be provided.
 *
 * Usernames are immutable and will be ignored if provided along with a userId.
 *
 * Only included fields will be updated. A null value will not set the value to empty.
 *
 * Created by MVW on 8/30/2017.
 */
public class UpdateUserRequest {

    /**
     * If a userId is provided, it will be used to locate the user
     */
    private final String userId;

    /**
     * If no userId is provided, the username will be used to locate the user
     */
    private final String username;

    private final String firstName;
    private final String lastName;
    private final String email;
    private final Boolean notificationsEnabled;

    /**
     * No-arg constructor for Jackson
     */
    private UpdateUserRequest() {
        this.userId = null;
        this.username = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.notificationsEnabled = null;
    }

    private UpdateUserRequest(Builder builder) {
        this.userId = builder.userId;
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.notificationsEnabled = builder.notificationsEnabled;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof UpdateUserRequest)) return false;
        UpdateUserRequest that = (UpdateUserRequest) o;
        return Objects.equals(notificationsEnabled, that.notificationsEnabled) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(username, that.username) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(userId, username, firstName, lastName, email, notificationsEnabled);
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public final static class Builder {
        private String userId;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private Boolean notificationsEnabled;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder notificationsEnabled(boolean notificationsEnabled) {
            this.notificationsEnabled = notificationsEnabled;
            return this;
        }

        public UpdateUserRequest build() {
            return new UpdateUserRequest(this);
        }
    }
}
