package com.thistroll.exceptions;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

/**
 * Exception for user not found
 *
 * Created by MVW on 8/26/2017.
 */
public class UserNotFoundException extends ResourceNotFoundException {
    private String customMessage;

    public UserNotFoundException(String message) {
        super(message);
        this.customMessage = message;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
