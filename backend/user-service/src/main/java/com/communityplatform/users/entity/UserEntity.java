package com.communityplatform.users.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * User entity representing a platform user.
 * 
 * This entity extends BaseEntity to inherit common auditing fields (id,
 * createdAt, updatedAt, deletedAt).
 * Each user has a unique username and email, along with profile information.
 */
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_deleted_at", columnList = "deleted_at")
})
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

    /** User's roles (many-to-many relationship) */
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<RoleEntity> roles = new HashSet<>();
}
