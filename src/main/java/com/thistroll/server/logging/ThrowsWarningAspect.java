package com.thistroll.server.logging;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Aspect for logging errors
 *
 * Created by MVW on 10/10/2017.
 */
public class ThrowsWarningAspect {

    private static final Logger LOGGER = LogManager.getLogger();

    void logThrowable(Throwable throwable) {
        LOGGER.warn(ExceptionUtils.getStackTrace(throwable));
    }
}
