package com.thistroll.exceptions;

/**
 * Exception for duplicate user names
 *
 * Created by MVW on 8/28/2017.
 */
public class DuplicateUsernameException extends IllegalArgumentException {
    public DuplicateUsernameException(String s) {
        super(s);
    }
}
