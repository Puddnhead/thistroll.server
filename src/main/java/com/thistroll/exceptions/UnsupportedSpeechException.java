package com.thistroll.exceptions;

/**
 * Exception for bad troll input
 *
 * Created by MVW on 7/26/2017.
 */
public class UnsupportedSpeechException extends IllegalArgumentException {
    public UnsupportedSpeechException(String s) {
        super(s);
    }
}
