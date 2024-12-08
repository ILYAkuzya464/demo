package com.example.demo.services;


import com.example.demo.model.User;

import java.util.Optional;


public interface UserService {
    User getById(Long id);
    Optional<User> findByEmail(String email);
    void create(String email, String name, String password) ;
}
