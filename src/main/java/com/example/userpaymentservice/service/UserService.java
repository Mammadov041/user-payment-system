package com.example.userpaymentservice.service;

import com.example.userpaymentservice.dto.CreateUserDTO;
import com.example.userpaymentservice.dto.UserDTO;
import com.example.userpaymentservice.entity.User;

import java.util.List;

public interface UserService {
    UserDTO addUser(CreateUserDTO dto);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, CreateUserDTO dto);
    void deleteUser(Long id);
    UserDTO mapToDTO(User user);
}