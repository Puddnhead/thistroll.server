package com.thistroll.exceptions;

/**
 * Exception generated someone attempts to create a user with the same email as a user already in the DB
 *
 * Created by MVW on 9/15/2017.
 */
public class DuplicateEmailException extends IllegalArgumentException {
    public DuplicateEmailException(String s) {
        super(s);
    }
}
