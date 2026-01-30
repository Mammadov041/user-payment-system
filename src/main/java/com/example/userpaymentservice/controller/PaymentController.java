package com.example.userpaymentservice.controller;

import com.example.userpaymentservice.dto.CreatePaymentDTO;
import com.example.userpaymentservice.dto.ErrorResponse;
import com.example.userpaymentservice.dto.PaymentDTO;
import com.example.userpaymentservice.dto.PaymentDetailDTO;
import com.example.userpaymentservice.entity.Payment;
import com.example.userpaymentservice.exception.InsufficientBalanceException;
import com.example.userpaymentservice.exception.PaymentNotFoundException;
import com.example.userpaymentservice.exception.UserNotFoundException;
import com.example.userpaymentservice.service.PaymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentServiceImpl paymentService;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentDTO dto) {
        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Creating payment - userId: {}, amount: {}", dto.userId(), dto.amount());
        } else {
            logger.info("Creating payment for user: {}", dto.userId());
        }

        try {
            Payment payment = new Payment(dto.userId(), dto.amount(), "PENDING");
            PaymentDTO result = paymentService.addPayment(payment);

            if("dev".equals(activeProfile)) {
                logger.info("DEV MODE: Payment created successfully - paymentId: {}, status: {}, newBalance: {}",
                        result.paymentId(), result.status(), result.balance());
            }

            return ResponseEntity.ok(result);
        } catch (InsufficientBalanceException e) {
            if("dev".equals(activeProfile)) {
                logger.error("DEV MODE: Insufficient balance error - {}", e.getMessage());
            }
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("INSUFFICIENT_BALANCE", e.getMessage()));
        } catch (UserNotFoundException e) {
            if("dev".equals(activeProfile)) {
                logger.error("DEV MODE: User not found - {}", e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<PaymentDetailDTO>> getAllPayments() {
        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Fetching all payments");
        }

        List<PaymentDetailDTO> payments = paymentService.getAllPayments();

        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Retrieved {} payments", payments.size());
        }

        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId) {
        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Fetching payment with id: {}", paymentId);
        }

        try {
            PaymentDetailDTO payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (PaymentNotFoundException e) {
            if("dev".equals(activeProfile)) {
                logger.error("DEV MODE: Payment not found - {}", e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("PAYMENT_NOT_FOUND", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPaymentsByUserId(@PathVariable Long userId) {
        if("dev".equals(activeProfile)) {
            logger.info("DEV MODE: Fetching payments for user: {}", userId);
        }

        try {
            List<PaymentDetailDTO> payments = paymentService.getPaymentsByUserId(userId);

            if("dev".equals(activeProfile)) {
                logger.info("DEV MODE: Retrieved {} payments for user {}", payments.size(), userId);
            }

            return ResponseEntity.ok(payments);
        } catch (UserNotFoundException e) {
            if("dev".equals(activeProfile)) {
                logger.error("DEV MODE: User not found - {}", e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
        }
    }

    // Controller-level exception handler (Method A)
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INSUFFICIENT_BALANCE", e.getMessage()));
    }
}