package com.thistroll.service.troll.api;

/**
 * Types of speech with different expected responses
 *
 * Created by MVW on 10/5/2017.
 */
public enum SpeechType {

    /**
     * Anything other than a question
     */
    STATEMENT,

    /**
     * Questions for which "yes" or "no" are sensible responses
     */
    YES_NO_QUESTION,

    /**
     * All other questions
     */
    OPEN_ENDED_QUESTION,
}
