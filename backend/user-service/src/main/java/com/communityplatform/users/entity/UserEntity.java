package com.communityplatform.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User entity representing a platform user.
 * 
 * This entity extends BaseEntity to inherit common auditing fields (id, createdAt, updatedAt, deletedAt).
 * Each user has a unique username and email, along with profile information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "users")
@Entity
public class UserEntity extends BaseEntity {

    /** Unique username for login (50 chars max) */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** Unique email address (100 chars max) */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** Encrypted password (required) */
    @Column(nullable = false)
    private String password;

    /** User's first name (50 chars max) */
    @Column(name = "first_name", length = 50)
    private String firstName;

    /** User's last name (50 chars max) */
    @Column(name = "last_name", length = 50)
    private String lastName;

    /** URL to user's profile picture (255 chars max) */
    @Column(name = "profile_picture_url", length = 255)
    private String profilePictureUrl;

    /** User's biography (500 chars max) */
    @Column(name = "bio", length = 500)
    private String bio;

}
