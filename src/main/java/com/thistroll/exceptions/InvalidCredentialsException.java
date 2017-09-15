package com.thistroll.exceptions;

import javax.security.auth.login.CredentialException;

/**
 * Custom class for generating 401s on bad credentials exceptions
 *
 * Created by MVW on 9/6/2017.
 */
public class InvalidCredentialsException extends CredentialException {
    public InvalidCredentialsException(String msg) {
        super(msg);
    }
}
