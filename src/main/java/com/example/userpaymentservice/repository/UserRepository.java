package com.example.userpaymentservice.repository;

import com.example.userpaymentservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
}