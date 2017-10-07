package com.thistroll.data.util;

import com.thistroll.exceptions.ValidationException;
import com.thistroll.domain.User;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing methods for validating user fields
 *
 * Created by MVW on 8/29/2017.
 */
public class UserValidationUtil {

    private static final String USERNAME_REQUIRED = "User must have a username";
    private static final String USERNAME_TOO_SHORT = "Username must be at least 4 characters";
    private static final String USERNAME_TOO_LONG = "Username cannot be longer than 64 characters";
    private static final String USERNAME_MUST_BE_ALPHANUMERIC = "Username must contain only the letters and numbers";
    private static final String FIRST_NAME_TOO_LONG = "First name can have a maximum of 128 characters";
    private static final String LAST_NAME_TOO_LONG = "Last name can have a maximum of 128 characters";
    private static final String EMAIL_REQUIRED = "User must provide an email address";
    private static final String EMAIL_TOO_LONG = "Email cannot contain more than 256 characters";
    private static final String EMAIL_INVALID = "Email is invalid";
    private static final String PASSWORD_REQUIRED = "User must have a password";
    private static final String PASSWORD_TOO_SHORT = "Password must be at least 6 characters long";
    private static final String PASSWORD_TOO_LONG = "Password must be no longer than 256 characters";

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Validates users according to the following rules:
     *      Username (required) must be between 4 and 64 characters and contain only alphanumeric characters
     *      Email (required) must be lest than 256 characters and in valid email format
     *      First name (optional) cannot be more than 128 characters
     *      Last name (optional) cannot be more than 128 characters
     *
     * @param user the user to validate
     * @throws ValidationException if the user has validation errors
     */
    public static void validateUser(User user) {
        List<String> validationErrors = new ArrayList<>();

        // Validate username
        String username = user.getUsername();
        if (StringUtils.isEmpty(username)) {
            validationErrors.add(USERNAME_REQUIRED);
        } else {
            if (username.length() < 4) {
                validationErrors.add(USERNAME_TOO_SHORT);
            } else if (username.length() > 256) {
                validationErrors.add(USERNAME_TOO_LONG);
            }
            if (!username.matches("\\w+")) {
                validationErrors.add(USERNAME_MUST_BE_ALPHANUMERIC);
            }
        }

        // Validate email
        String email = user.getEmail();
        if (StringUtils.isEmpty(email)) {
            validationErrors.add(EMAIL_REQUIRED);
        } else {
            if (email.length() > 256) {
                validationErrors.add(EMAIL_TOO_LONG);
            }
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
            if (!matcher.find()) {
                validationErrors.add(EMAIL_INVALID);
            }
        }

        // Validate first and last name
        if (!StringUtils.isEmpty(user.getFirstName()) && user.getFirstName().length() > 128) {
            validationErrors.add(FIRST_NAME_TOO_LONG);
        }
        if (!StringUtils.isEmpty(user.getLastName()) && user.getLastName().length() > 128) {
            validationErrors.add(LAST_NAME_TOO_LONG);
        }

        if (!validationErrors.isEmpty()) {
            String message = StringUtils.join(validationErrors, ',');
            throw new ValidationException(message);
        }
    }

    /**
     * Validates a password according to the following rules
     *      Password must be between 6 and 256 characters
     *
     * @param password the password to validate
     */
    public static void validatePassword(String password) {
        if (StringUtils.isEmpty(password)) {
            throw new ValidationException(PASSWORD_REQUIRED);
        } else if (password.length() < 6) {
            throw new ValidationException(PASSWORD_TOO_SHORT);
        } else if (password.length() > 256) {
            throw new ValidationException(PASSWORD_TOO_LONG);
        }
    }
}
