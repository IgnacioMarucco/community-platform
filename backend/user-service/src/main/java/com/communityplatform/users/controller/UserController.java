package com.communityplatform.users.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.communityplatform.users.dto.user.UserCreateDto;
import com.communityplatform.users.dto.user.UserResponseDto;
import com.communityplatform.users.dto.user.UserUpdateDto;
import com.communityplatform.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for user management.
 * Provides CRUD endpoints for users with proper HTTP status codes.
 */
@RestController
@RequestMapping("${api.base-path}/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserService userService;

    /**
     * Create a new user.
     * POST /api/v1/users
     * 
     * @param createDto user creation data (validated)
     * @return 201 CREATED with user data
     */
    @Operation(summary = "Create a new user", description = "Creates a new user account with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User with username or email already exists")
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto createDto) {
        log.info("POST /api/v1/users - Creating user with username: {}", createDto.getUsername());
        UserResponseDto response = userService.createUser(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get user by ID.
     * GET /api/v1/users/{id}
     * 
     * @param id user ID
     * @return 200 OK with user data
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("GET /api/v1/users/{} - Fetching user", id);
        UserResponseDto response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active users.
     * GET /api/v1/users
     * 
     * @return 200 OK with list of users
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all active users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        log.info("GET /api/v1/users - Fetching all users");
        List<UserResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing user.
     * PUT /api/v1/users/{id}
     * 
     * @param id        user ID
     * @param updateDto user update data (validated)
     * @return 200 OK with updated user data
     */
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto updateDto) {
        log.info("PUT /api/v1/users/{} - Updating user", id);
        UserResponseDto response = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft delete a user.
     * DELETE /api/v1/users/{id}
     * 
     * @param id user ID
     * @return 204 NO CONTENT
     */
    @Operation(summary = "Delete user", description = "Soft deletes a user (marks as inactive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/v1/users/{} - Deleting user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
