package com.thistroll.troll.api;

/**
 * Resolves some speech into a known type
 *
 * Created by MVW on 10/5/2017.
 */
public interface SpeechTypeResolver {

    /**
     * Analyzes some speech to determine the most appropriate type of response
     *
     * @param speech the speech to analyze
     * @return the type of speech according to expected response
     */
    SpeechType resolve(String speech);
}
