package com.thistroll.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by MVW on 8/31/2017.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DeleteFailedException extends IllegalArgumentException {
    public DeleteFailedException(String s) {
        super(s);
    }
}
