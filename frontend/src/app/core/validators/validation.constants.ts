export const USERNAME_MIN_LENGTH = 3;
export const USERNAME_MAX_LENGTH = 50;
export const USERNAME_PATTERN = /^[a-zA-Z0-9_-]+$/;
export const USERNAME_REQUIRED_MESSAGE = 'Username is required';
export const USERNAME_SIZE_MESSAGE = 'Username must be between 3 and 50 characters';
export const USERNAME_PATTERN_MESSAGE =
  'Username can only contain letters, numbers, underscores, and hyphens';

export const EMAIL_MAX_LENGTH = 100;
export const EMAIL_REQUIRED_MESSAGE = 'Email is required';
export const EMAIL_INVALID_MESSAGE = 'Email must be valid';
export const EMAIL_SIZE_MESSAGE = 'Email must not exceed 100 characters';

export const PASSWORD_MIN_LENGTH = 8;
export const PASSWORD_SPECIAL_CHARS = '@$!%*?&#';
export const PASSWORD_PATTERN =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/;
export const PASSWORD_REQUIRED_MESSAGE = 'Password is required';
export const PASSWORD_SIZE_MESSAGE = 'Password must be at least 8 characters';
export const PASSWORD_PATTERN_MESSAGE =
  'Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&#)';

export const NAME_MAX_LENGTH = 50;
export const FIRST_NAME_SIZE_MESSAGE = 'First name must not exceed 50 characters';
export const LAST_NAME_SIZE_MESSAGE = 'Last name must not exceed 50 characters';
