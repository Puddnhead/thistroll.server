package com.thistroll.server.logging;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Annotation for logging exceptions as errors
 *
 * Created by MVW on 10/10/2017.
 */
public class ThrowsErrorAspect {
    private static final Logger LOGGER = LogManager.getLogger();

    void logThrowable(Throwable throwable) {
        LOGGER.error(ExceptionUtils.getStackTrace(throwable));
    }
}
