package com.example.userpaymentservice.service;

public interface LoggingService {
    void logPaymentCreation(Long userId, double amount);
    void logPaymentSuccess(Long paymentId, String status, double balance);
    void logError(String message);
}