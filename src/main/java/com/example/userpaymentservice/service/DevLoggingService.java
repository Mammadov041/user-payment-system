package com.example.userpaymentservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class DevLoggingService implements LoggingService {
    private static final Logger logger = LoggerFactory.getLogger(DevLoggingService.class);

    @Override
    public void logPaymentCreation(Long userId, double amount) {
        logger.debug("🔵 DEV: Creating payment for user {} with amount {}", userId, amount);
        logger.debug("🔵 DEV: Timestamp: {}", System.currentTimeMillis());
        logger.debug("🔵 DEV: Thread: {}", Thread.currentThread().getName());
    }

    @Override
    public void logPaymentSuccess(Long paymentId, String status, double balance) {
        logger.debug("✅ DEV: Payment {} completed with status: {}", paymentId, status);
        logger.debug("✅ DEV: New balance: {}", balance);
        logger.debug("✅ DEV: Full details logged");
    }

    @Override
    public void logError(String message) {
        logger.error("❌ DEV: Error occurred: {}", message);
        logger.error("❌ DEV: Stack trace available in logs");
    }
}