package com.example.userpaymentservice.service;

import com.example.userpaymentservice.dto.PaymentDTO;
import com.example.userpaymentservice.dto.PaymentDetailDTO;
import com.example.userpaymentservice.entity.Payment;
import com.example.userpaymentservice.entity.User;
import com.example.userpaymentservice.exception.InsufficientBalanceException;
import com.example.userpaymentservice.exception.PaymentNotFoundException;
import com.example.userpaymentservice.exception.UserNotFoundException;
import com.example.userpaymentservice.repository.PaymentRepositoryImpl;
import com.example.userpaymentservice.repository.UserRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepositoryImpl paymentRepository;
    private final UserRepositoryImpl userRepository;

    public PaymentServiceImpl(PaymentRepositoryImpl paymentRepository, UserRepositoryImpl userRepository){
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Payment addPayment(Payment payment) {
        // 1. Get user
        User user = userRepository.findById(payment.getUser_id());
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }

        // 2. Check balance
        double newBalance = user.getBalance() - payment.getAmount();
        if(newBalance < 0) {
            throw new InsufficientBalanceException("User balance is not enough");
        }

        // 4. Update user balance
        user.setBalance(newBalance);
        userRepository.update(user);

        // 5. Update payment status to SUCCESS
        // Note: Since we don't have the generated ID, we'll need to modify this
        // For now, we'll assume the payment object gets the ID after creation
        payment.setStatus("SUCCESS");
        // 3. Create payment with SUCCESS status
        int result = paymentRepository.create(payment);
        if(result <= 0) {
            throw new RuntimeException("Payment could not be created");
        }

        // 6. Return payment
        return paymentRepository.getUsersLastPayment(payment.getUser_id());
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        var payment = paymentRepository.findById(id);
        if(payment == null) {
            throw new PaymentNotFoundException("Payment not found");
        }
        return (payment);
    }

    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        var user = userRepository.findById(userId);
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }
        return paymentRepository.findByUserId(userId);
    }

    @Override
    public PaymentDTO mapToDTO(Payment payment) {
        var user = userRepository.findById(payment.getUser_id());
        return new PaymentDTO(payment.getId(),payment.getStatus(),user.getBalance());
    }

    @Override
    public PaymentDetailDTO mapToDetailedDTO(Payment payment) {
        return new PaymentDetailDTO(payment.getId(),payment.getUser_id(),payment.getAmount(),payment.getStatus(),payment.getCreatedAt());
    }

    private PaymentDetailDTO mapToDetailDTO(Payment payment) {
        return new PaymentDetailDTO(
                payment.getId(),
                payment.getUser_id(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt() != null ? payment.getCreatedAt() : null
        );
    }
}