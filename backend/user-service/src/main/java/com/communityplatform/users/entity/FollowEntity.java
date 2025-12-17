package com.communityplatform.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Follow relationship entity representing user-to-user following connections.
 * 
 * This entity extends BaseEntity to inherit common auditing fields (id, createdAt, updatedAt, deletedAt).
 * Enforces a unique constraint on (follower_id, following_id) to prevent duplicate follow relationships.
 */
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "follower_id", "following_id" })
})
@Entity
public class FollowEntity extends BaseEntity {

    /** ID of the user doing the following */
    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    /** ID of the user being followed */
    @Column(name = "following_id", nullable = false)
    private Long followingId;

}
