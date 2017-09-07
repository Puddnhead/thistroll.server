package com.thistroll.service.client.dto;

import com.thistroll.domain.enums.UserRole;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for a create user request
 *
 * Created by MVW on 8/26/2017.
 */
public class CreateUserRequest {

    private final String username;

    private final Set<UserRole> roles;

    private final String email;

    private final String firstName;

    private final String lastName;

    private final String password;

    private final boolean notificationsEnabled;

    /**
     * No-arg constructor for Jackson
     */
    private CreateUserRequest() {
        this.username = null;
        this.roles = null;
        this.email = null;
        this.firstName = null;
        this.lastName = null;
        this.password = null;
        this.notificationsEnabled = false;
    }

    private CreateUserRequest(Builder builder) {
        this.username = builder.username;
        this.roles = builder.roles;
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.password = builder.password;
        this.notificationsEnabled = builder.notificationsEnabled;
    }

    public String getUsername() {
        return username;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof CreateUserRequest)) return false;
        CreateUserRequest that = (CreateUserRequest) o;
        return notificationsEnabled == that.notificationsEnabled &&
                Objects.equals(username, that.username) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(email, that.email) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(password, that.password);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(username, roles, email, firstName, lastName, password, notificationsEnabled);
    }

    public static final class Builder {
        private String username;
        private Set<UserRole> roles = Collections.emptySet();
        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private boolean notificationsEnabled = false;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder roles(Set<UserRole> roles) {
            this.roles = roles;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
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

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder notificationsEnabled(boolean notificationsEnabled) {
            this.notificationsEnabled = notificationsEnabled;
            return this;
        }

        public CreateUserRequest build() {
            return new CreateUserRequest(this);
        }
    }
}
