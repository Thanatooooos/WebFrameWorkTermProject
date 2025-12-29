package com.example.ecommerce_demo.service;

import com.example.ecommerce_demo.entity.User;

public interface UserService {
    User findByUserName(String username);

    User findByEmail(String email);

    void save(User user);
}
