package com.example.userpaymentservice.service;

import com.example.userpaymentservice.dto.CreateUserDTO;
import com.example.userpaymentservice.dto.UserDTO;
import com.example.userpaymentservice.entity.User;

import com.example.userpaymentservice.exception.UserNotFoundException;
import com.example.userpaymentservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(CreateUserDTO dto) {
        User user = new User();
        user.setFullName(dto.fullName());
        user.setBalance(dto.balance());
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.orElseThrow(()-> new UserNotFoundException("User was not found"));
        return user.get();
    }

    @Override
    public User updateUser(Long id, CreateUserDTO dto) {
        Optional<User> user = userRepository.findById(id);
        user.orElseThrow(()->new UserNotFoundException("User was not found"));
        user.get().setFullName(dto.fullName());
        user.get().setBalance(dto.balance());
        return userRepository.save(user.get());
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.orElseThrow(() -> new UserNotFoundException("User was not found"));
        userRepository.delete(user.get());
    }

    @Override
    public UserDTO mapToDTO(User user) {
        return new UserDTO(user.getId(), user.getFullName());
    }
}