package com.example.userpaymentservice.service;

import com.example.userpaymentservice.dto.PaymentDTO;
import com.example.userpaymentservice.dto.PaymentDetailDTO;
import com.example.userpaymentservice.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment addPayment(Payment payment);
    List<Payment> getAllPayments();
    Payment getPaymentById(Long id);
    List<Payment> getPaymentsByUserId(Long userId);
    PaymentDTO mapToDTO(Payment payment);
    PaymentDetailDTO mapToDetailedDTO(Payment payment);
}