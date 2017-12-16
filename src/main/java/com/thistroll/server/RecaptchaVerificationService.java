package com.thistroll.server;

/**
 * Simple service for verifying a google recaptcha response
 *
 * Created by MVW on 12/15/2017.
 */
public interface RecaptchaVerificationService {

    /**
     * Verify a google recaptcha response
     *
     * @param gRecaptchaResponse the recaptcha response
     * @return true if the response is valid else false
     */
    boolean verify(String gRecaptchaResponse);
}
