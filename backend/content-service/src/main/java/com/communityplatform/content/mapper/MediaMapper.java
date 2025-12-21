package com.communityplatform.content.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.communityplatform.content.dto.media.MediaResponseDto;
import com.communityplatform.content.dto.media.MediaUploadResponseDto;
import com.communityplatform.content.entity.MediaEntity;

/**
 * MapStruct mapper for Media entity and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MediaMapper {

    /**
     * Convert Entity to UploadResponseDto.
     */
    MediaUploadResponseDto toUploadResponseDto(MediaEntity entity);

    /**
     * Convert Entity to full ResponseDto.
     */
    MediaResponseDto toResponseDto(MediaEntity entity);
}
