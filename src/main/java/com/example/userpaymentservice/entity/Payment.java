package com.example.userpaymentservice.entity;

import java.sql.Timestamp;

public class Payment {
    private Long id;
    private Long userId;
    private double amount;
    private String status;
    private Timestamp createdAt;

    public Payment(){}

    public Payment(Long id, Long userId, double amount, String status, Timestamp createdAt){
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Payment(Long userId, double amount, String status){
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public Long getUser_id() {
        return userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }
}