package com.example.userpaymentservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class ProdLoggingService implements LoggingService {
    private static final Logger logger = LoggerFactory.getLogger(ProdLoggingService.class);

    @Override
    public void logPaymentCreation(Long userId, double amount) {
        logger.info("Payment creation initiated for user {}", userId);
    }

    @Override
    public void logPaymentSuccess(Long paymentId, String status, double balance) {
        logger.info("Payment {} completed: {}", paymentId, status);
    }

    @Override
    public void logError(String message) {
        logger.error("Error: {}", message);
    }
}