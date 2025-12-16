package com.communityplatform.users.entity;

import com.communityplatform.users.enums.RoleName;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Role entity representing user roles in the platform.
 * 
 * This entity extends BaseEntity to inherit common auditing fields (id, createdAt, updatedAt, deletedAt).
 * Roles are stored as enum strings (ROLE_USER, ROLE_ADMIN) in the database.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "roles")
@Entity
public class RoleEntity extends BaseEntity {

    /** Role name stored as string enum (e.g., ROLE_USER, ROLE_ADMIN) */
    @Enumerated(EnumType.STRING)
    private RoleName roleName;
}
