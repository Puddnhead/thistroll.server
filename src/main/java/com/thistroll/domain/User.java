package com.thistroll.domain;

import com.thistroll.domain.enums.UserRole;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notEmpty;

/**
 * Created by MVW on 8/22/2017.
 */
public class User extends AbstractPersistentObject {

    /**
     * No partitions for now, because I can't think of any sensible way of doing it
     */
    public static final String PARTITION_KEY_NAME = "PARTITION_KEY";
    public static final String PARTITION_KEY_VALUE = "NO_PARTITIONS";

    public static final String USERNAME_PROPERTY_NAME = "username";
    public static final String ROLES_PROPERTY_NAME = "roles";
    public static final String EMAIL_PROPERTY_NAME = "email";
    public static final String FIRST_NAME_PROPERTY_NAME = "firstName";
    public static final String LAST_NAME_PROPERTY_NAME = "lastName";
    public static final String NOTIFICATIONS_PROPERTY_NAME = "notifactionsEnabled";

    public static final String USERNAME_INDEX = "usernameIndex";

    private final String username;

    private final Set<UserRole> roles;

    private final String email;

    private final String firstName;

    private final String lastName;

    private final boolean notificationsEnabled;

    /**
     * No-arg constructor for Jackson
     */
    public User() {
        super();
        this.username = null;
        this.roles = null;
        this.email = null;
        this.firstName = null;
        this.lastName = null;
        this.notificationsEnabled = false;
    }

    public User(User.Builder builder) {
        super(builder);
        this.username = builder.username;
        this.roles = builder.roles;
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
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

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return user.canEqual(this) &&
                notificationsEnabled == user.notificationsEnabled &&
                Objects.equals(username, user.username) &&
                Objects.equals(roles, user.roles) &&
                Objects.equals(email, user.email) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(super.hashCode(), username, roles, email, firstName, lastName, notificationsEnabled, super.hashCode());
    }

    public boolean canEqual(Object o) {
        return o instanceof User;
    }

    public static final class Builder extends AbstractPersistentObject.Builder<Builder> {
        private String username;
        private Set<UserRole> roles = Collections.emptySet();
        private String email;
        private String firstName;
        private String lastName;
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

        public Builder notificationsEnabled(boolean notificationsEnabled) {
            this.notificationsEnabled = notificationsEnabled;
            return this;
        }

        private void validate() {
            notEmpty(username);
            notEmpty(email);
        }

        public User build() {
            validate();
            return new User(this);
        }
    }
}
