package com.thistroll.service.client;

import com.thistroll.domain.Speech;
import com.thistroll.domain.enums.Outcome;

/**
 * Service for talking to trolls
 *
 * Created by MVW on 7/21/2017.
 */
public interface TrollService {

    /**
     * Returns a troll's response to the given speech
     *
     * @param speech any type of speech
     * @return some trollspeak
     */
    String trollSpeak(String speech);

    /**
     * Returns a persisted speech with the given text
     *
     * @param text text
     * @return a persisted speech with the given text
     */
    Speech getSpeechByText(String text);

    /**
     * Delete a speech that matches the given ID or text
     *
     * @param idOrText either a speech id or text
     * @return success if a a speech was deleted
     */
    Outcome deleteSpeech(String idOrText);

    /**
     * Returns the next speech with no responses
     *
     * @return the next speech with no responses
     */
    Speech getNextSpeechWithNoResponse();

    /**
     * Update the responses for the provided speech
     *
     * @param speech the speech to update
     * @return the updated speech
     */
    Speech updateResponses(Speech speech);

    /**
     * Return the total number of speeches without known responses
     *
     * @return the total number of speeches without known responses
     */
    int getSpeechWithoutResponsesCount();

    /**
     * Return the total number of speeches in the repository for which a response is known
     *
     * @return the total number of speeches in the repository for which a response is known
     */
    int getKnownSpeechCount();
}
