package com.communityplatform.users.service;

import java.util.List;

import com.communityplatform.users.dto.user.UserCreateDto;
import com.communityplatform.users.dto.user.UserResponseDto;
import com.communityplatform.users.dto.user.UserUpdateDto;

/**
 * Service interface for user management operations.
 */
public interface UserService {

    /**
     * Create a new user.
     * 
     * @param createDto user creation data
     * @return created user response
     * @throws DuplicateUserException if username or email already exists
     */
    UserResponseDto createUser(UserCreateDto createDto);

    /**
     * Get user by ID.
     * 
     * @param userId user ID
     * @return user response
     * @throws UserNotFoundException if user not found or deleted
     */
    UserResponseDto getUserById(Long userId);

    /**
     * Get all active users.
     * 
     * @return list of active users
     */
    List<UserResponseDto> getAllUsers();

    /**
     * Update an existing user.
     * 
     * @param userId    user ID
     * @param updateDto user update data
     * @return updated user response
     * @throws UserNotFoundException  if user not found or deleted
     * @throws DuplicateUserException if username or email already exists
     */
    UserResponseDto updateUser(Long userId, UserUpdateDto updateDto);

    /**
     * Soft delete a user (sets deletedAt timestamp).
     * 
     * @param userId user ID
     * @throws UserNotFoundException if user not found or already deleted
     */
    void deleteUser(Long userId);
}
