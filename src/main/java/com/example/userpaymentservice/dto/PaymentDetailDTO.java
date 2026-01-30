package com.example.userpaymentservice.dto;

import java.time.LocalDateTime;

public record PaymentDetailDTO(
        Long paymentId,
        Long userId,
        double amount,
        String status,
        LocalDateTime createdAt
) {
}