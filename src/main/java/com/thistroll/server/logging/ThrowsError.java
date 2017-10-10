package com.thistroll.server.logging;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for methods that throw an exception that should be logged as an error
 *
 * Created by MVW on 10/10/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ThrowsError {
}
