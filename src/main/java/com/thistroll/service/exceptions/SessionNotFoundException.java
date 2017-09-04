package com.thistroll.service.exceptions;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for failed session retrievals
 *
 * Created by MVW on 9/3/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SessionNotFoundException extends ResourceNotFoundException {
    public SessionNotFoundException(String message) {
        super(message);
    }
}
