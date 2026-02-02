package com.example.userpaymentservice.service;

import com.example.userpaymentservice.dto.CreateUserDTO;
import com.example.userpaymentservice.dto.UserDTO;
import com.example.userpaymentservice.entity.User;
import com.example.userpaymentservice.exception.UserNotFoundException;
import com.example.userpaymentservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserDTO createUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("John Doe");
        testUser.setBalance(1000.0);

        createUserDTO = new CreateUserDTO("John Doe", 1000.0);
    }

    @Test
    @DisplayName("Should successfully add a new user")
    void addUser_Success() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.addUser(createUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals(1000.0, result.getBalance());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should return all users")
    void getAllUsers_Success() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setFullName("Jane Smith");
        user2.setBalance(2000.0);

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        assertEquals("Jane Smith", result.get(1).getFullName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void getAllUsers_EmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should successfully get user by ID")
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getFullName());
        assertEquals(1000.0, result.getBalance());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found by ID")
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(999L)
        );
        assertEquals("User was not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should successfully update user")
    void updateUser_Success() {
        // Arrange
        CreateUserDTO updateDTO = new CreateUserDTO("John Updated", 1500.0);
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFullName("John Updated");
        updatedUser.setBalance(1500.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("John Updated", result.getFullName());
        assertEquals(1500.0, result.getBalance());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating non-existent user")
    void updateUser_NotFound() {
        // Arrange
        CreateUserDTO updateDTO = new CreateUserDTO("John Updated", 1500.0);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(999L, updateDTO)
        );
        assertEquals("User was not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully delete user")
    void deleteUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void deleteUser_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(999L)
        );
        assertEquals("User was not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should correctly map User entity to UserDTO")
    void mapToDTO_Success() {
        // Act
        UserDTO result = userService.mapToDTO(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John Doe", result.fullName());
    }

    @Test
    @DisplayName("Should handle user with null values in DTO mapping")
    void mapToDTO_NullValues() {
        // Arrange
        User userWithNulls = new User();
        userWithNulls.setId(null);
        userWithNulls.setFullName(null);

        // Act
        UserDTO result = userService.mapToDTO(userWithNulls);

        // Assert
        assertNotNull(result);
        assertNull(result.id());
        assertNull(result.fullName());
    }
}