package com.example.userpaymentservice.service;

import com.example.userpaymentservice.dto.PaymentDTO;
import com.example.userpaymentservice.dto.PaymentDetailDTO;
import com.example.userpaymentservice.entity.Payment;
import com.example.userpaymentservice.entity.User;
import com.example.userpaymentservice.exception.InsufficientBalanceException;
import com.example.userpaymentservice.exception.PaymentNotFoundException;
import com.example.userpaymentservice.exception.UserNotFoundException;
import com.example.userpaymentservice.repository.PaymentRepository;
import com.example.userpaymentservice.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
   private final UserRepository userRepository;
   private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(UserRepository userRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Payment addPayment(Payment payment) {
        // 1. Get user
        Optional<User> user = userRepository.findById(payment.getUserId());
        user.orElseThrow(()-> new UserNotFoundException("User not found"));

        // 2. Check balance
        double newBalance = user.get().getBalance() - payment.getAmount();
        if(newBalance < 0) {
            throw new InsufficientBalanceException("User balance is not enough");
        }

        // 3. Update user balance
        user.get().setBalance(newBalance);
        userRepository.save(user.get());

        payment.setStatus("SUCCESS");

        // 4. Return payment
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        var payment = paymentRepository.findById(id);
        payment.orElseThrow(() -> new PaymentNotFoundException("Payment was not found"));
        return payment.get();
    }

    @Override
    public List<Payment> getPaymentsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User was not found"));

        return paymentRepository.findByUserId(
                userId,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }


    @Override
    public PaymentDTO mapToDTO(Payment payment) {
        Optional<User> user = userRepository.findById(payment.getUserId());
        user.orElseThrow(()-> new UserNotFoundException("User not found"));
        return new PaymentDTO(payment.getId(),payment.getStatus(),user.get().getBalance(),payment.getAmount());
    }

    @Override
    public PaymentDetailDTO mapToDetailedDTO(Payment payment) {
        return new PaymentDetailDTO(payment.getId(),payment.getUserId(),payment.getAmount(),payment.getStatus(),payment.getCreatedAt());
    }

    private PaymentDetailDTO mapToDetailDTO(Payment payment) {
        return new PaymentDetailDTO(
                payment.getId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt() != null ? payment.getCreatedAt() : null
        );
    }
}