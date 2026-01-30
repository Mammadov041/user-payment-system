package com.example.userpaymentservice.repository;

import com.example.userpaymentservice.entity.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();
    User findById(Long id);
    int create(User user);
    int update(User user);
    int delete(Long id);
}