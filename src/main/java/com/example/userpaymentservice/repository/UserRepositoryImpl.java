package com.example.userpaymentservice.repository;

import com.example.userpaymentservice.entity.User;
import com.example.userpaymentservice.mapper.UserRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository{
    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        var query = "SELECT * FROM users ORDER BY id";
        return jdbcTemplate.query(query, new UserRowMapper());
    }

    @Override
    public User findById(Long id) {
        var query = "SELECT * FROM users WHERE id=?";
        List<User> users = jdbcTemplate.query(query, new UserRowMapper(), id);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public int create(User user) {
        var query = "INSERT INTO users (full_name, balance) VALUES(?, ?)";
        return jdbcTemplate.update(query, user.getFullName(), user.getBalance());
    }

    @Override
    public int update(User user) {
        var query = "UPDATE users SET full_name=?, balance=? WHERE id=?";
        return jdbcTemplate.update(query, user.getFullName(), user.getBalance(), user.getId());
    }

    @Override
    public int delete(Long id) {
        var query = "DELETE FROM users WHERE id=?";
        return jdbcTemplate.update(query, id);
    }
}