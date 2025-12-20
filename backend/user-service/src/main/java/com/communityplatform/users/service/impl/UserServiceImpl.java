package com.communityplatform.users.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.communityplatform.users.dto.user.ChangePasswordDto;
import com.communityplatform.users.dto.user.UserCreateDto;
import com.communityplatform.users.dto.user.UserResponseDto;
import com.communityplatform.users.dto.user.UserUpdateDto;
import com.communityplatform.users.entity.UserEntity;
import com.communityplatform.users.exception.BadCredentialsException;
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
    private final PasswordEncoder passwordEncoder;

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

        // Convert DTO to Entity
        UserEntity entity = userMapper.toEntity(createDto);
        
        // Encrypt password before saving
        entity.setPassword(passwordEncoder.encode(createDto.getPassword()));
        
        // Save with proper exception handling for race conditions
        try {
            UserEntity savedEntity = userRepository.save(entity);
            log.info("User created successfully with id: {}", savedEntity.getId());
            return userMapper.toResponseDto(savedEntity);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Handle race condition where user was created between check and save
            log.error("Data integrity violation while creating user: {}", ex.getMessage());
            if (ex.getMessage().contains("username")) {
                throw new DuplicateUserException("username", createDto.getUsername());
            } else if (ex.getMessage().contains("email")) {
                throw new DuplicateUserException("email", createDto.getEmail());
            }
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        log.info("Fetching user with id: {}", userId);

        UserEntity entity = userRepository.findByIdWithRoles(userId)
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

        // Use optimized query to filter at database level
        List<UserEntity> entities = userRepository.findAllByDeletedAtIsNull();

        return userMapper.toResponseDtoList(entities);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserUpdateDto updateDto) {
        log.info("Updating user with id: {}", userId);

        UserEntity entity = userRepository.findByIdWithRoles(userId)
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
        
        // Save with proper exception handling for race conditions
        try {
            UserEntity updatedEntity = userRepository.save(entity);
            log.info("User updated successfully with id: {}", userId);
            return userMapper.toResponseDto(updatedEntity);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            log.error("Data integrity violation while updating user: {}", ex.getMessage());
            if (ex.getMessage().contains("username")) {
                throw new DuplicateUserException("username", updateDto.getUsername());
            } else if (ex.getMessage().contains("email")) {
                throw new DuplicateUserException("email", updateDto.getEmail());
            }
            throw ex;
        }
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Soft deleting user with id: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

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
    
    @Override
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
        log.info("Changing password for user with id: {}", userId);
        
        UserEntity entity = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Check if user is soft deleted
        if (!entity.isActive()) {
            throw new UserNotFoundException("User with id " + userId + " has been deleted");
        }
        
        // Verify current password
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), entity.getPassword())) {
            log.warn("Invalid current password provided for user id: {}", userId);
            throw new BadCredentialsException("Current password is incorrect");
        }
        
        // Encrypt and set new password
        entity.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(entity);
        
        log.info("Password changed successfully for user with id: {}", userId);
    }
}
