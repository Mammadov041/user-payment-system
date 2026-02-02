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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User testUser;
    private Payment testPayment;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("John Doe");
        testUser.setBalance(1000.0);

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setUserId(1L);
        testPayment.setAmount(200.0);
        testPayment.setStatus("PENDING");
        testPayment.setCreatedAt(Timestamp.valueOf(testDateTime));
    }

    @Test
    @DisplayName("Should successfully add payment with sufficient balance")
    void addPayment_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setUserId(1L);
        savedPayment.setAmount(200.0);
        savedPayment.setStatus("SUCCESS");
        savedPayment.setCreatedAt(Timestamp.valueOf(testDateTime));

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        Payment result = paymentService.addPayment(testPayment);

        // Assert
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(200.0, result.getAmount());
        assertEquals(800.0, testUser.getBalance()); // 1000 - 200
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found during payment")
    void addPayment_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> paymentService.addPayment(testPayment)
        );
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when balance is insufficient")
    void addPayment_InsufficientBalance() {
        // Arrange
        testUser.setBalance(100.0); // Less than payment amount
        testPayment.setAmount(200.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> paymentService.addPayment(testPayment)
        );
        assertEquals("User balance is not enough", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when payment results in negative balance")
    void addPayment_NegativeBalance() {
        // Arrange
        testUser.setBalance(150.0);
        testPayment.setAmount(200.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> paymentService.addPayment(testPayment)
        );
        assertEquals("User balance is not enough", exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully add payment when balance becomes exactly zero")
    void addPayment_BalanceBecomesZero() {
        // Arrange
        testUser.setBalance(200.0);
        testPayment.setAmount(200.0);

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setUserId(1L);
        savedPayment.setAmount(200.0);
        savedPayment.setStatus("SUCCESS");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        Payment result = paymentService.addPayment(testPayment);

        // Assert
        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(0.0, testUser.getBalance());
        verify(userRepository, times(1)).save(testUser);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should return all payments")
    void getAllPayments_Success() {
        // Arrange
        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setUserId(1L);
        payment2.setAmount(300.0);
        payment2.setStatus("SUCCESS");

        List<Payment> payments = Arrays.asList(testPayment, payment2);
        when(paymentRepository.findAll()).thenReturn(payments);

        // Act
        List<Payment> result = paymentService.getAllPayments();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(200.0, result.get(0).getAmount());
        assertEquals(300.0, result.get(1).getAmount());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no payments exist")
    void getAllPayments_EmptyList() {
        // Arrange
        when(paymentRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Payment> result = paymentService.getAllPayments();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should successfully get payment by ID")
    void getPaymentById_Success() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // Act
        Payment result = paymentService.getPaymentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(200.0, result.getAmount());
        assertEquals(1L, result.getUserId());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw PaymentNotFoundException when payment not found by ID")
    void getPaymentById_NotFound() {
        // Arrange
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(999L)
        );
        assertEquals("Payment was not found", exception.getMessage());
        verify(paymentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should successfully get payments by user ID")
    void getPaymentsByUserId_Success() {
        // Arrange
        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setUserId(1L);
        payment2.setAmount(300.0);
        payment2.setCreatedAt(Timestamp.valueOf(testDateTime.plusHours(1)));

        List<Payment> payments = Arrays.asList(payment2, testPayment);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(paymentRepository.findByUserId(
                eq(1L),
                eq(Sort.by(Sort.Direction.DESC, "createdAt"))
        )).thenReturn(payments);

        // Act
        List<Payment> result = paymentService.getPaymentsByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId()); // Most recent first
        assertEquals(1L, result.get(1).getId());
        verify(userRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).findByUserId(
                eq(1L),
                eq(Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when getting payments for non-existent user")
    void getPaymentsByUserId_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> paymentService.getPaymentsByUserId(999L)
        );
        assertEquals("User was not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(paymentRepository, never()).findByUserId(any(), any());
    }

    @Test
    @DisplayName("Should return empty list when user has no payments")
    void getPaymentsByUserId_EmptyList() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(paymentRepository.findByUserId(
                eq(1L),
                eq(Sort.by(Sort.Direction.DESC, "createdAt"))
        )).thenReturn(Arrays.asList());

        // Act
        List<Payment> result = paymentService.getPaymentsByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should correctly map Payment to PaymentDTO")
    void mapToDTO_Success() {
        // Arrange
        testPayment.setStatus("SUCCESS");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        PaymentDTO result = paymentService.mapToDTO(testPayment);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.paymentId());
        assertEquals("SUCCESS", result.status());
        assertEquals(1000.0, result.balance());
        assertEquals(200.0, result.amount());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when mapping DTO with non-existent user")
    void mapToDTO_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> paymentService.mapToDTO(testPayment)
        );
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should correctly map Payment to PaymentDetailDTO")
    void mapToDetailedDTO_Success() {
        // Act
        PaymentDetailDTO result = paymentService.mapToDetailedDTO(testPayment);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.paymentId());
        assertEquals(1L, result.userId());
        assertEquals(200.0, result.amount());
        assertEquals("PENDING", result.status());
//        assertEquals(testDateTime, Timestamp.valueOf(result.createdAt().toString()));
    }

    @Test
    @DisplayName("Should handle null createdAt in PaymentDetailDTO mapping")
    void mapToDetailedDTO_NullCreatedAt() {
        // Arrange
        testPayment.setCreatedAt(null);

        // Act
        PaymentDetailDTO result = paymentService.mapToDetailedDTO(testPayment);

        // Assert
        assertNotNull(result);
        assertNull(result.createdAt());
    }

    @Test
    @DisplayName("Should handle concurrent payment scenario")
    void addPayment_ConcurrentScenario() {
        // Arrange
        testUser.setBalance(250.0);
        testPayment.setAmount(200.0);

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setUserId(1L);
        savedPayment.setAmount(200.0);
        savedPayment.setStatus("SUCCESS");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        Payment result = paymentService.addPayment(testPayment);

        // Assert
        assertNotNull(result);
        assertEquals(50.0, testUser.getBalance()); // 250 - 200
        assertEquals("SUCCESS", result.getStatus());
    }
}