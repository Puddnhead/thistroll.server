package com.thistroll.service.exceptions;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by MVW on 7/22/2017.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class BlogNotFoundException extends ResourceNotFoundException {
    public BlogNotFoundException(String message) {
        super(message);
    }
}
