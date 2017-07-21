package com.thistroll.service.client;

/**
 * Service for talking to trolls
 *
 * Created by MVW on 7/21/2017.
 */
public interface TrollService {

    /**
     * Returns a troll's response to the given statement
     *
     * @param statement and sort of statement
     * @return some trollspeak
     */
    String trollSpeak(String statement);
}
