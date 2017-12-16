package com.thistroll.service.client.dto.request;

import java.util.Objects;

/**
 * DTO for a user registration request
 *
 * Created by MVW on 8/26/2017.
 */
public class RegisterUserRequest {

    private final String username;

    private final String email;

    private final String firstName;

    private final String lastName;

    private final String password;

    private final boolean notificationsEnabled;

    private final String grecaptchaResponse;

    /**
     * No-arg constructor for Jackson
     */
    private RegisterUserRequest() {
        this.username = null;
        this.email = null;
        this.firstName = null;
        this.lastName = null;
        this.password = null;
        this.notificationsEnabled = false;
        this.grecaptchaResponse = null;
    }

    private RegisterUserRequest(Builder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.password = builder.password;
        this.notificationsEnabled = builder.notificationsEnabled;
        this.grecaptchaResponse = builder.grecaptchaResponse;
    }

    public String getUsername() {
        return username;
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

    public String getGrecaptchaResponse() {
        return grecaptchaResponse;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof RegisterUserRequest)) return false;
        RegisterUserRequest that = (RegisterUserRequest) o;
        return notificationsEnabled == that.notificationsEnabled &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(password, that.password) &&
                Objects.equals(grecaptchaResponse, that.grecaptchaResponse);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(username, email, firstName, lastName, password, notificationsEnabled, grecaptchaResponse);
    }

    public static final class Builder {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private boolean notificationsEnabled = false;
        private String grecaptchaResponse;

        public Builder username(String username) {
            this.username = username;
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

        public Builder grecaptchaResponse(String grecaptchaResponse) {
            this.grecaptchaResponse = grecaptchaResponse;
            return this;
        }

        public RegisterUserRequest build() {
            return new RegisterUserRequest(this);
        }
    }
}
