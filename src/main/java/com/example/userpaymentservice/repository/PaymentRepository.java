package com.example.userpaymentservice.repository;

import com.example.userpaymentservice.entity.Payment;

import java.util.List;

public interface PaymentRepository {
    List<Payment> findAll();
    List<Payment> findByUserId(Long userId);
    Payment findById(Long paymentId);
    int create(Payment payment);
    int updateStatus(Long paymentId, String status);
}