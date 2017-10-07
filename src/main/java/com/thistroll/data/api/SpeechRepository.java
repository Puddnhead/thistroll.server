package com.thistroll.data.api;

import com.thistroll.domain.Speech;
import com.thistroll.domain.enums.Outcome;

/**
 * Speech repository interface shared by multiple impementations
 *
 * Created by MVW on 10/6/2017.
 */
public interface SpeechRepository {

    /**
     * Fetch a speech object by ID - which is a hash of the text
     *
     * @param id the id of the speech object, which is a hash of the text
     * @return the speech or null if it cannot be found
     */
    Speech getSpeechById(String id);

    /**
     * Fetch a speech by generating the id from the provided text
     *
     * @param text text to use to generate an id
     * @return the speech or null if it cannot be found
     */
    Speech getSpeechByText(String text);

    /**
     * Saves a new speech, generating createdOn, lastUpdatedOn, and id fields. The id is a hash of the text.
     *
     * @param speech the speech to create
     * @return the created speech with generated fields
     */
    Speech saveSpeech(Speech speech);

    /**
     * Updates the speech with the supplied responses, ignoring all other fields. Also auto-generates a new
     * lastUpdatedOn field.
     *
     * @param speech the speech to update
     * @return the updated speech with new responses and lastUpdatedOn fields
     */
    Speech updateSpeech(Speech speech);

    /**
     * Removes a speech from the repository
     *
     * @param id the id of the speech
     * @return SUCCESS if the speech was found or FAILURE if no speech was found
     */
    Outcome deleteSpeech(String id);

    /**
     * Returns an arbitrary speech from the repository. This is used for scanning a repository to supply or update
     * answers.
     *
     * @return an arbitrary speech from the repository
     */
    Speech getNextSpeech();

    /**
     * Returns a count of the total number of speeches in the repository.
     * Note: This is not necessarily accurate, as it uses the AWS "DescribeTable" API which is only updated every
     *      6 hours.
     *
     * @return the total number of speeches in the repository
     */
    int getTotalNumberOfSpeeches();
}
