package com.thistroll.exceptions;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

/**
 * Exception for failed session retrievals
 *
 * Created by MVW on 9/3/2017.
 */
public class SessionNotFoundException extends ResourceNotFoundException {

    private String customMessage;

    public SessionNotFoundException(String message) {
        super(message);
        this.customMessage = message;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
