package com.thistroll.exceptions;

/**
 * Exception for failed delete operations
 *
 * Created by MVW on 8/31/2017.
 */
public class DeleteFailedException extends IllegalArgumentException {
    public DeleteFailedException(String s) {
        super(s);
    }
}
