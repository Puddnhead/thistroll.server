package com.thistroll.service.troll.repositories;

/**
 * Simple inteface for fetching randomd responses
 *
 * Created by MVW on 10/5/2017.
 */
public interface RandomResponseRepository {

    /**
     * Fetch a random response
     *
     * @return a random response
     */
    String getRandomResponse();
}
