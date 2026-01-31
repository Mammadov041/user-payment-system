package com.example.userpaymentservice.dto;

import java.sql.Timestamp;

public record PaymentDetailDTO(
        Long paymentId,
        Long userId,
        double amount,
        String status,
        Timestamp createdAt
) {
}