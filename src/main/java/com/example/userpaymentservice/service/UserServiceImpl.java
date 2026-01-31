package com.example.userpaymentservice.service;

import com.example.userpaymentservice.dto.CreateUserDTO;
import com.example.userpaymentservice.dto.UserDTO;
import com.example.userpaymentservice.entity.User;
import com.example.userpaymentservice.exception.UserNotFoundException;
import com.example.userpaymentservice.repository.UserRepositoryImpl;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepository;

    public UserServiceImpl(UserRepositoryImpl userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(CreateUserDTO dto) {
        User user = new User();
        user.setFullName(dto.fullName());
        user.setBalance(dto.balance());

        int result = userRepository.create(user);
        if(result <= 0) {
            throw new RuntimeException("User could not be created");
        }

        // Since we don't get the ID back, we'll fetch the user by name
        // In production, you'd use RETURNING clause or GeneratedKeyHolder
        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(u -> u.getFullName().equals(dto.fullName()))
                .reduce((first, second) -> second) // Get the last one
                .orElseThrow(() -> new RuntimeException("User created but not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        User user = userRepository.findById(id);
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public User updateUser(Long id, CreateUserDTO dto) {
        User user = userRepository.findById(id);
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }

        user.setFullName(dto.fullName());
        user.setBalance(dto.balance());

        int result = userRepository.update(user);
        if(result <= 0) {
            throw new RuntimeException("User could not be updated");
        }

        return user;
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id);
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }

        int result = userRepository.delete(id);
        if(result <= 0) {
            throw new RuntimeException("User could not be deleted");
        }
    }

    @Override
    public UserDTO mapToDTO(User user) {
        return new UserDTO(user.getId(), user.getFullName());
    }
}