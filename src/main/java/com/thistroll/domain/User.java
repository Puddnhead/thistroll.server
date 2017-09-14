package com.thistroll.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thistroll.domain.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * A user object
 *
 * Created by MVW on 8/22/2017.
 */
public class User extends AbstractPersistentObject implements UserDetails {

    /**
     * No partitions for now, because I can't think of any sensible way of doing it
     */
    public static final String PARTITION_KEY_NAME = "PARTITION_KEY";
    public static final String PARTITION_KEY_VALUE = "NO_PARTITIONS";

    public static final String USERNAME_PROPERTY = "username";
    public static final String ROLES_PROPERTY = "roles";
    public static final String EMAIL_PROPERTY = "email";
    public static final String FIRST_NAME_PROPERTY = "firstName";
    public static final String LAST_NAME_PROPERTY = "lastName";
    public static final String NOTIFICATIONS_PROPERTY = "notificationsEnabled";
    public static final String PASSWORD_PROPERTY = "password";

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

    private User(User.Builder builder) {
        super(builder);
        this.username = builder.username;
        this.roles = builder.roles;
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.notificationsEnabled = builder.notificationsEnabled;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        if (roles != null) {
            roles.stream()
                    .forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role.name())));
        }

        return grantedAuthorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
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

        public User build() {
            return new User(this);
        }
    }
}
