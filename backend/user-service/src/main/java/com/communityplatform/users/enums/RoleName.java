package com.communityplatform.users.enums;

/**
 * Enum representing available user roles in the platform.
 * 
 * Roles are persisted as strings in the database using @Enumerated(EnumType.STRING).
 */
public enum RoleName {
    /** Standard user role with basic permissions */
    ROLE_USER,
    /** Administrator role with full permissions */
    ROLE_ADMIN
}