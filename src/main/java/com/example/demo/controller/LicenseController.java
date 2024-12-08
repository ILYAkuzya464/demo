package com.example.demo.controller;


import com.example.demo.controller.dto.LicenseActivationRequest;
import com.example.demo.controller.dto.LicenseRequest;
import com.example.demo.controller.dto.UpdateLicenseRequest;
import com.example.demo.model.License;
import com.example.demo.model.User;
import com.example.demo.services.LicenseService;
import com.example.demo.services.UserService;
import com.example.demo.ticket.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@RequiredArgsConstructor
@RequestMapping("/licenses")
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class LicenseController {
    private final LicenseService licenseService;
    private final UserService userService;

    @PostMapping("/activate")//есть
    public ResponseEntity<?> activateLicense(@RequestBody LicenseActivationRequest request) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            Ticket fullTicket = licenseService.activateLicense(request, authenticatedUser);
            return ResponseEntity.ok(fullTicket);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")//есть
    public ResponseEntity<String> add(@RequestBody LicenseRequest licenseRequest) {
        try {
            License createdLicense = licenseService.createLicense(
                    licenseRequest.getProductId(),
                    licenseRequest.getOwnerId(),
                    licenseRequest.getLicenseTypeId());
            return ResponseEntity.ok("Лицензия успешно создана с ID: " + createdLicense.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при создании лицензии: " + e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/renew")
    public ResponseEntity<?> renewLicense(@RequestBody UpdateLicenseRequest updateLicenseRequest) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            Ticket ticket = licenseService.renewLicense(updateLicenseRequest, authenticatedUser);
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeLicense(@PathVariable Long id) {
        try {
            License license = licenseService.findById(id);
            if (license == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Лицензия не найдена.");
            }
            if (license.getActivationDate() != null) {
                return ResponseEntity.badRequest().body("Невозможно удалить активированную лицензию.");
            }
            licenseService.deleteById(id);
            return ResponseEntity.ok("Лицензия успешно удалена.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getLicenseInfo(@RequestParam String mac) {
        return licenseService.getLicenseInfo(mac);
    }

    @PatchMapping("/block/{licenseId}")
    public ResponseEntity<String> changeLicenseStatus(
            @PathVariable Long licenseId,
            @RequestParam boolean isBlocked) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            licenseService.changeLicenseStatus(licenseId, isBlocked, authenticatedUser);
            return ResponseEntity.ok("Статус лицензии успешно обновлен.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Лицензия с таким ID не найдена");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при изменении статуса лицензии: " + e.getMessage());
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