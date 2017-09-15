package com.thistroll.exceptions;

/**
 * Generic validation exception
 *
 * Created by MVW on 8/29/2017.
 */
public class ValidationException extends IllegalArgumentException {
    public ValidationException(String s) {
        super(s);
    }
}
