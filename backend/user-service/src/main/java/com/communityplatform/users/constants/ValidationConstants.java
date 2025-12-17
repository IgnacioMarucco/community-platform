package com.communityplatform.users.constants;

/**
 * Centralized validation constants for user fields.
 * Used across DTOs and validation logic.
 */
public final class ValidationConstants {

    // Private constructor to prevent instantiation
    private ValidationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Username constraints
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]+$";
    public static final String USERNAME_PATTERN_MESSAGE = "Username can only contain letters, numbers, underscores, and hyphens";

    // Email constraints
    public static final int EMAIL_MAX_LENGTH = 100;

    // Password constraints
    public static final int PASSWORD_MIN_LENGTH = 8;

    // Name constraints
    public static final int NAME_MAX_LENGTH = 50;

    // Profile fields
    public static final int PROFILE_PICTURE_URL_MAX_LENGTH = 255;
    public static final int BIO_MAX_LENGTH = 500;

    // Validation messages
    public static final String USERNAME_REQUIRED = "Username is required";
    public static final String USERNAME_SIZE = "Username must be between " + USERNAME_MIN_LENGTH + " and "
            + USERNAME_MAX_LENGTH + " characters";

    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Email must be valid";
    public static final String EMAIL_SIZE = "Email must not exceed " + EMAIL_MAX_LENGTH + " characters";

    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_SIZE = "Password must be at least " + PASSWORD_MIN_LENGTH + " characters";

    public static final String FIRST_NAME_SIZE = "First name must not exceed " + NAME_MAX_LENGTH + " characters";
    public static final String LAST_NAME_SIZE = "Last name must not exceed " + NAME_MAX_LENGTH + " characters";
    public static final String PROFILE_PICTURE_SIZE = "Profile picture URL must not exceed "
            + PROFILE_PICTURE_URL_MAX_LENGTH + " characters";
    public static final String BIO_SIZE = "Bio must not exceed " + BIO_MAX_LENGTH + " characters";
}
