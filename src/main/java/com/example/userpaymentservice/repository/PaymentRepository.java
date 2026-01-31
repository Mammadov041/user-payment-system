package com.example.userpaymentservice.repository;

import com.example.userpaymentservice.entity.Payment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findByUserId(Long userId, Sort sort);

}