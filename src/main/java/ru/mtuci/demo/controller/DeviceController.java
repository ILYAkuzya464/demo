package ru.mtuci.demo.controller;

import ru.mtuci.demo.model.User;
import ru.mtuci.demo.services.DeviceService;
import ru.mtuci.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/device")
@RestController
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<String> addDevice(@RequestParam String name) {
        try {
            User user = getAuthenticatedUser();
            deviceService.addDevice(name, user);
            return ResponseEntity.ok("Устройство успешно добавлено");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = (String) authentication.getPrincipal();
            return userService.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
        return null;
    }
}
