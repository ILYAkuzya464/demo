package ru.mtuci.demo.services;


import ru.mtuci.demo.model.User;

import java.util.Optional;


public interface UserService {
    User getById(Long id);
    Optional<User> findByEmail(String email);
    void create(String email, String name, String password) ;
}
