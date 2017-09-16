package com.thistroll.service.client.dto.request;

/**
 * DTO because passing emails as path parameters is wonky
 *
 * Created by MVW on 9/15/2017.
 */
public class GetUserByEmailRequest {

    private final String email;

    private GetUserByEmailRequest() {
        this.email = null;
    }

    public GetUserByEmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
