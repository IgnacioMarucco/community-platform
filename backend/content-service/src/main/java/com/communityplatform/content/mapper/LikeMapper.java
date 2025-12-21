package com.communityplatform.content.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.communityplatform.content.dto.like.LikeResponseDto;
import com.communityplatform.content.entity.LikeEntity;

/**
 * MapStruct mapper for Like entity and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LikeMapper {

    /**
     * Convert Entity to ResponseDto.
     */
    @Mapping(target = "username", ignore = true)
    LikeResponseDto toResponseDto(LikeEntity entity);
}
