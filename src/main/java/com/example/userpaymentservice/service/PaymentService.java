package com.example.userpaymentservice.service;

import com.example.userpaymentservice.dto.PaymentDTO;
import com.example.userpaymentservice.dto.PaymentDetailDTO;
import com.example.userpaymentservice.entity.Payment;

import java.util.List;

public interface PaymentService {
    PaymentDTO addPayment(Payment payment);
    List<PaymentDetailDTO> getAllPayments();
    PaymentDetailDTO getPaymentById(Long id);
    List<PaymentDetailDTO> getPaymentsByUserId(Long userId);
    PaymentDTO mapToDTO(Payment payment);
}