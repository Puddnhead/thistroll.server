package com.thistroll.service.troll.api;

/**
 * Simple interface for normalizing speech
 *
 * Created by MVW on 10/6/2017.
 */
public interface SpeechNormalizationService {

    /**
     * Returns normalized text according to the following rules:
     *  - all in lowercase: I love you -> i love you
     *  - never use proper contractions: i am -> i'm
     *      Note: Currently unable to normalize in ambiguous in 'd cases: I'd = I had OR I would
     *      TODO: Detect participles to resolve ambiguity in 'd case
     *  - known slang replaced with proper English: gonna -> going to
     *  - convert '\t', '\r', and '\n' to ' '
     *  - all punctuation stripped except for ' ', '?', and '-'
     *  - '?' appended to speech determined to be a question by the speech type resolver
     *
     * @param speech the input speech
     * @return normalized speech
     */
    String normalize(String speech);
}
