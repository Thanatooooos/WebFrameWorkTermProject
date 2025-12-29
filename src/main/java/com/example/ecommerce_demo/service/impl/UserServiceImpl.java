package com.example.ecommerce_demo.service.impl;

import com.example.ecommerce_demo.entity.User;
import com.example.ecommerce_demo.repository.UserRepository;
import com.example.ecommerce_demo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUserName(String username){
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    public void save(User user){
        userRepository.save(user);
    }
}
