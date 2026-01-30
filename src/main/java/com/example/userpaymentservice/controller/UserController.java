package com.example.userpaymentservice.controller;

import com.example.userpaymentservice.dto.CreateUserDTO;
import com.example.userpaymentservice.dto.ErrorResponse;
import com.example.userpaymentservice.dto.UserDTO;
import com.example.userpaymentservice.exception.UserNotFoundException;
import com.example.userpaymentservice.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserServiceImpl userService;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserDTO dto) {
        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Creating user - fullName: {}, balance: {}", dto.fullName(), dto.balance());
        } else {
            logger.info("Creating user: {}", dto.fullName());
        }

        try {
            // Validate balance
            if(dto.balance() < 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_BALANCE", "Balance cannot be negative"));
            }

            UserDTO result = userService.addUser(dto);

            if("dev".equals(activeProfile)) {
                logger.info("DEV MODE: User created successfully - userId: {}, fullName: {}",
                        result.id(), result.fullName());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            if("dev".equals(activeProfile)) {
                logger.error("DEV MODE: Error creating user - {}", e.getMessage(), e);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("CREATION_ERROR", "User could not be created"));
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Fetching all users");
        }

        List<UserDTO> users = userService.getAllUsers();

        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Retrieved {} users", users.size());
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Fetching user with id: {}", id);
        }

        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            if("dev".equals(activeProfile)) {
                logger.error("DEV MODE: User not found - {}", e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody CreateUserDTO dto) {
        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Updating user {} - fullName: {}, balance: {}",
                    id, dto.fullName(), dto.balance());
        } else {
            logger.info("Updating user: {}", id);
        }

        try {
            // Validate balance
            if(dto.balance() < 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_BALANCE", "Balance cannot be negative"));
            }

            UserDTO result = userService.updateUser(id, dto);

            if("dev".equals(activeProfile)) {
                logger.info("DEV MODE: User updated successfully - userId: {}, fullName: {}",
                        result.id(), result.fullName());
            }

            return ResponseEntity.ok(result);
        } catch (UserNotFoundException e) {
            if("dev".equals(activeProfile)) {
                logger.error("DEV MODE: User not found - {}", e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Deleting user with id: {}", id);
        } else {
            logger.info("Deleting user: {}", id);
        }

        try {
            userService.deleteUser(id);

            if("dev".equals(activeProfile)) {
                logger.info("DEV MODE: User deleted successfully - userId: {}", id);
            }

            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            if("dev".equals(activeProfile)) {
                logger.error("DEV MODE: User not found - {}", e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
        }
    }
}