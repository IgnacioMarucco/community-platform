package com.communityplatform.users.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.communityplatform.users.dto.user.UserCreateDto;
import com.communityplatform.users.dto.user.UserResponseDto;
import com.communityplatform.users.dto.user.UserUpdateDto;
import com.communityplatform.users.entity.UserEntity;

/**
 * MapStruct mapper for converting between UserEntity and DTOs.
 * 
 * MapStruct generates the implementation automatically at compile time.
 * componentModel = "spring" makes this a Spring bean that can be @Autowired.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Convert UserEntity to UserResponseDto.
     * Maps all fields including audit fields from BaseEntity.
     * Roles are not included in UserResponseDto (no target field).
     */
    UserResponseDto toResponseDto(UserEntity entity);

    /**
     * Convert UserCreateDto to UserEntity.
     * ID and audit fields will be set by JPA/BaseEntity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    UserEntity toEntity(UserCreateDto createDto);

    /**
     * Update an existing UserEntity from UserUpdateDto.
     * Only non-null fields from the DTO will be updated (IGNORE strategy).
     * 
     * @param entity    the entity to update
     * @param updateDto the DTO with update data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(@MappingTarget UserEntity entity, UserUpdateDto updateDto);

    /**
     * Convert a list of UserEntities to UserResponseDtos.
     */
    List<UserResponseDto> toResponseDtoList(List<UserEntity> entities);
}
