package com.thistroll.service.exceptions;

/**
 * Created by MVW on 7/26/2017.
 */
public class UnsupportedSpeechException extends IllegalArgumentException {
    public UnsupportedSpeechException(String s) {
        super(s);
    }
}
