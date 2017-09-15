package com.thistroll.exceptions;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

/**
 * Exceptions for blogs that cannot be found
 *
 * Created by MVW on 7/22/2017.
 */
public class BlogNotFoundException extends ResourceNotFoundException {

    private String customMessage;

    public BlogNotFoundException(String message) {
        super(message);
        this.customMessage = message;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
