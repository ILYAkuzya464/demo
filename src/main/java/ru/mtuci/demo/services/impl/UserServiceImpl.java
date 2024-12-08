package ru.mtuci.demo.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationRole;
import ru.mtuci.demo.model.User;
import ru.mtuci.demo.repo.UserRepository;
import ru.mtuci.demo.services.UserService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void create(String email, String name, String password)  {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Пользователь с таким email уже существует: " + email);
        }

        var user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(ApplicationRole.USER);
        userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(new User());
    }

    @Override
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

}