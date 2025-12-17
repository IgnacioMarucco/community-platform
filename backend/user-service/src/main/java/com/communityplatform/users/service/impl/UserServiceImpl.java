package com.communityplatform.users.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.communityplatform.users.dto.user.UserCreateDto;
import com.communityplatform.users.dto.user.UserResponseDto;
import com.communityplatform.users.dto.user.UserUpdateDto;
import com.communityplatform.users.entity.UserEntity;
import com.communityplatform.users.exception.DuplicateUserException;
import com.communityplatform.users.exception.UserNotFoundException;
import com.communityplatform.users.mapper.UserMapper;
import com.communityplatform.users.repository.UserRepository;
import com.communityplatform.users.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of UserService with full CRUD operations.
 * Uses MapStruct for entity-DTO conversions and implements soft delete.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(UserCreateDto createDto) {
        log.info("Creating user with username: {}", createDto.getUsername());

        // Validate uniqueness
        if (userRepository.existsByUsername(createDto.getUsername())) {
            throw new DuplicateUserException("username", createDto.getUsername());
        }
        if (userRepository.existsByEmail(createDto.getEmail())) {
            throw new DuplicateUserException("email", createDto.getEmail());
        }

        // Convert DTO to Entity and save
        UserEntity entity = userMapper.toEntity(createDto);
        UserEntity savedEntity = userRepository.save(entity);

        log.info("User created successfully with id: {}", savedEntity.getId());
        return userMapper.toResponseDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        log.info("Fetching user with id: {}", userId);

        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Check if user is soft deleted
        if (!entity.isActive()) {
            throw new UserNotFoundException("User with id " + userId + " has been deleted");
        }

        return userMapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        log.info("Fetching all active users");

        List<UserEntity> entities = userRepository.findAll().stream()
                .filter(UserEntity::isActive) // Only return active users
                .toList();

        return userMapper.toResponseDtoList(entities);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserUpdateDto updateDto) {
        log.info("Updating user with id: {}", userId);

        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Check if user is soft deleted
        if (!entity.isActive()) {
            throw new UserNotFoundException("User with id " + userId + " has been deleted");
        }

        // Check uniqueness if username or email are being updated
        if (updateDto.getUsername() != null &&
                !updateDto.getUsername().equals(entity.getUsername()) &&
                userRepository.existsByUsername(updateDto.getUsername())) {
            throw new DuplicateUserException("username", updateDto.getUsername());
        }

        if (updateDto.getEmail() != null &&
                !updateDto.getEmail().equals(entity.getEmail()) &&
                userRepository.existsByEmail(updateDto.getEmail())) {
            throw new DuplicateUserException("email", updateDto.getEmail());
        }

        // Update entity (MapStruct will only update non-null fields)
        userMapper.updateEntity(entity, updateDto);
        UserEntity updatedEntity = userRepository.save(entity);

        log.info("User updated successfully with id: {}", userId);
        return userMapper.toResponseDto(updatedEntity);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Soft deleting user with id: {}", userId);

        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Check if already deleted
        if (!entity.isActive()) {
            throw new UserNotFoundException("User with id " + userId + " is already deleted");
        }

        // Soft delete using BaseEntity's helper method
        entity.softDelete();
        userRepository.save(entity);

        log.info("User soft deleted successfully with id: {}", userId);
    }
}
