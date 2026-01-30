package com.example.userpaymentservice.repository;

import com.example.userpaymentservice.entity.Payment;
import com.example.userpaymentservice.mapper.PaymentRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    private final JdbcTemplate jdbcTemplate;

    public PaymentRepositoryImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Payment> findAll() {
        String query = "SELECT * FROM payments ORDER BY created_at DESC";
        return jdbcTemplate.query(query, new PaymentRowMapper());
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        String query = "SELECT * FROM payments WHERE user_id=? ORDER BY created_at DESC";
        return jdbcTemplate.query(query, new PaymentRowMapper(), userId);
    }

    @Override
    public Payment findById(Long paymentId) {
        String query = "SELECT * FROM payments WHERE id=?";
        List<Payment> payments = jdbcTemplate.query(query, new PaymentRowMapper(), paymentId);
        return payments.isEmpty() ? null : payments.get(0);
    }

    @Override
    public int create(Payment payment) {
        String query = "INSERT INTO payments(user_id, amount, status) VALUES(?, ?, ?)";
        return jdbcTemplate.update(query, payment.getUser_id(), payment.getAmount(), payment.getStatus());
    }

    public int updateStatus(Long paymentId, String status) {
        String query = "UPDATE payments SET status=? WHERE id=?";
        return jdbcTemplate.update(query, status, paymentId);
    }
}