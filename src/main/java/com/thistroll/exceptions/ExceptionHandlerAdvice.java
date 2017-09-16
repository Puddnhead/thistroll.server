package com.thistroll.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class responsible for generating consumable error messages for various exception types
 *
 * Created by MVW on 9/15/2017.
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler
    public ResponseEntity handleException(DuplicateUsernameException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity handleException(DuplicateEmailException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity handleException(DeleteFailedException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity handleException(BlogNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getCustomMessage()));
    }

    @ExceptionHandler
    public ResponseEntity handleException(InvalidCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity handleException(SessionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getCustomMessage()));
    }

    @ExceptionHandler
    public ResponseEntity handleException(UnsupportedSpeechException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity handleException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getCustomMessage()));
    }

    @ExceptionHandler
    public ResponseEntity handleException(ValidationException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorMessage(e.getMessage()));
    }
}
