package com.communityplatform.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.communityplatform.users.entity.RoleEntity;
import com.communityplatform.users.enums.RoleName;

/**
 * Repository for RoleEntity operations.
 */
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Find a role by its name.
     * 
     * @param roleName the role name enum
     * @return Optional containing the role if found
     */
    Optional<RoleEntity> findByRoleName(RoleName roleName);
}
