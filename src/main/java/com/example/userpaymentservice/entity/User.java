package com.example.userpaymentservice.entity;

public class User {
    private Long id;
    private String fullName;
    private double balance;

    public User(){}
    public User(Long id,String fullName,double  balance){
        this.id = id;
        this.fullName = fullName;
        this.balance = balance;
    }

    // setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // getters
    public Long getId() {
        return this.id;
    }

    public String getFullName(){
        return this.fullName;
    }

    public double getBalance(){
        return this.balance;
    }
}
