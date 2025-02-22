package ru.mtuci.demo.controller;

import org.springframework.web.bind.annotation.*;
import ru.mtuci.demo.controller.dto.ChangePassRequest;
import ru.mtuci.demo.model.ApplicationRole;
import ru.mtuci.demo.model.User;
import ru.mtuci.demo.repo.LicenseRepository;
import ru.mtuci.demo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.mtuci.demo.services.UserService;

import java.util.Optional;

//TODO: 1. Один пользователь может удалить другого? - Админ может удалить любого пользователя, юзер теперь удалить сможет только себя

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final LicenseRepository licenseRepository;

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeUser(@RequestParam String email) {
        try {
            User authenticatedUser = userService.getAuthenticatedUser();

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь с email " + email + " не найден");
            }
            User user = userOptional.get();

            boolean isAdmin = authenticatedUser.getRole() == ApplicationRole.ADMIN;
            if (!isAdmin && !authenticatedUser.getEmail().equals(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Вы можете удалить только свой аккаунт");
            }

            boolean hasLicenses = licenseRepository.existsByUserId(user.getId());
            if (hasLicenses) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Нельзя удалить пользователя с активными лицензиями");
            }

            userRepository.delete(user);

            return ResponseEntity.ok("Пользователь успешно удален");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassRequest request) {
        try {
            User user = userService.getAuthenticatedUser();
            userService.changePassword(request.getOldPassword(), request.getNewPassword(),user);
            return ResponseEntity.ok("Пароль успешно изменен");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

}
