package com.thistroll.exceptions;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

/**
 * Exception for a missing speech
 *
 * Created by MVW on 10/6/2017.
 */
public class SpeechNotFoundException extends ResourceNotFoundException {

    private String customMessage;

    public SpeechNotFoundException(String message) {
        super(message);
        this.customMessage = message;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
