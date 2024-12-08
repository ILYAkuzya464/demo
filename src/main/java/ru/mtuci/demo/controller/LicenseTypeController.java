package ru.mtuci.demo.controller;

import ru.mtuci.demo.model.LicenseType;
import ru.mtuci.demo.services.LicenseService;
import ru.mtuci.demo.services.LicenseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@RestController
@RequestMapping("/license-type")
public class LicenseTypeController {

    private final LicenseTypeService licenseTypeService;
    private final LicenseService licenseService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<String> addLicenseType(@RequestBody LicenseType licenseType) {
        try {
            LicenseType createdLicenseType = licenseTypeService.addLicenseType(licenseType);
            return ResponseEntity.ok("Тип лицензии успешно создан с ID: " + createdLicenseType.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при создании типа лицензии: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeLicenseType(@PathVariable Long id) {
        try {
            if (licenseService.existsByLicenseTypeId(id)) {
                return ResponseEntity.badRequest().body("Невозможно удалить LicenseType, так как существуют лицензии, использующие этот тип.");
            }
            licenseTypeService.deleteById(id);
            return ResponseEntity.ok("LicenseType успешно удалён.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/view/all")
    public ResponseEntity<List<LicenseType>> getAllLicenseTypes() {
        try {
            List<LicenseType> licenseTypes = licenseTypeService.findAll();
            if (licenseTypes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(licenseTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<LicenseType> getLicenseTypeById(@PathVariable Long id) {
        try {
            Optional<LicenseType> licenseType = licenseTypeService.findById(id);
            if (licenseType.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(licenseType.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
