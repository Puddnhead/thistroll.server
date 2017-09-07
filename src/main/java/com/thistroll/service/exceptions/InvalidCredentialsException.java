package com.thistroll.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.security.auth.login.CredentialException;

/**
 * Custom class for generating 401s on bad credentials exceptions
 *
 * Created by MVW on 9/6/2017.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends CredentialException {
    public InvalidCredentialsException(String msg) {
        super(msg);
    }
}
