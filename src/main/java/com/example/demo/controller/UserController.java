package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.LicenseRepository;
import com.example.demo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserRepository userRepository;
    private final LicenseRepository licenseRepository;

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь с ID " + userId + " не найден");
            }

            User user = userOptional.get();

            boolean hasLicenses = licenseRepository.existsByUserId(userId);
            if (hasLicenses) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Нельзя удалить пользователя с активными или завершенными лицензиями");
            }

            userRepository.delete(user);

            return ResponseEntity.ok("Пользователь с ID " + userId + " успешно удален");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }
}
