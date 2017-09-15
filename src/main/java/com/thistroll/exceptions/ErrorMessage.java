package com.thistroll.exceptions;

/**
 * Brutally simple POJO for serializing error messages
 *
 * Created by MVW on 9/15/2017.
 */
public class ErrorMessage {
    private String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
