package com.thistroll.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by MVW on 8/28/2017.
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class DuplicateUsernameException extends IllegalArgumentException {
}
