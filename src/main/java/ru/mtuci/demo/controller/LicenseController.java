package ru.mtuci.demo.controller;


import ru.mtuci.demo.controller.dto.HistoryRequest;
import ru.mtuci.demo.controller.dto.LicenseActivationRequest;
import ru.mtuci.demo.controller.dto.LicenseRequest;
import ru.mtuci.demo.controller.dto.UpdateLicenseRequest;
import ru.mtuci.demo.model.License;
import ru.mtuci.demo.model.LicenseHistory;
import ru.mtuci.demo.model.User;
import ru.mtuci.demo.services.LicenseHistoryService;
import ru.mtuci.demo.services.LicenseService;
import ru.mtuci.demo.services.UserService;
import ru.mtuci.demo.ticket.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RequiredArgsConstructor
@RequestMapping("/licenses")
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class LicenseController {
    private final LicenseService licenseService;
    private final UserService userService;
    private final LicenseHistoryService licenseHistoryService;

    @PostMapping("/activate")//есть
    public ResponseEntity<?> activateLicense(@RequestBody LicenseActivationRequest request) {
        try {
            User authenticatedUser = userService.getAuthenticatedUser();
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
            User authenticatedUser = userService.getAuthenticatedUser();
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

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/block/{licenseId}")
    public ResponseEntity<String> changeLicenseStatus(
            @PathVariable Long licenseId,
            @RequestParam boolean isBlocked) {
        try {
            User authenticatedUser = userService.getAuthenticatedUser();
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

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/history")
    public ResponseEntity<List<LicenseHistory>> getLicenseHistoryByKey(@RequestBody HistoryRequest historyRequest) {
        try {
            List<LicenseHistory> history = licenseHistoryService.getHistoryByLicenseKey(historyRequest.getKey());

            if (history.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/history/clear-all")
    public ResponseEntity<String> deleteAllHistory() {
        try {
            licenseHistoryService.deleteAllHistory();
            return ResponseEntity.ok("Вся история лицензий удалена.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/history/clear-by-key")
    public ResponseEntity<String> deleteHistoryByLicenseKey(@RequestBody HistoryRequest historyRequest) {
        try {
            licenseHistoryService.deleteHistoryByLicenseKey(historyRequest.getKey());
            return ResponseEntity.ok("История для лицензии удалена.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

}