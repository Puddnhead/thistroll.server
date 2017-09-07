package com.thistroll.service.client.dto;

import java.util.Objects;

/**
 * Simple DTO for login requests
 *
 * Created by MVW on 9/5/2017.
 */
public class LoginRequest {

    private final String username;

    private final String password;

    /**
     * No-arg constructor for Jackson
     */
    private LoginRequest() {
        this.username = null;
        this.password = null;
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof LoginRequest)) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(username, password);
    }
}
