package com.example.userpaymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties
public class UserPaymentServiceApplication {
    static void main(String[] args) {
        SpringApplication.run(UserPaymentServiceApplication.class, args);
    }
}