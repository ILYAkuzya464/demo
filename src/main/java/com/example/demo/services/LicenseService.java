package com.example.demo.services;

import com.example.demo.controller.dto.LicenseActivationRequest;
import com.example.demo.controller.dto.UpdateLicenseRequest;
import com.example.demo.model.License;
import com.example.demo.model.User;
import com.example.demo.ticket.Ticket;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface LicenseService {
    void add(License license);
    License getByKey(String key);
    License createLicense(Long productId, Long ownerId, Long licenseTypeId);
    License findById(Long id);
    Ticket activateLicense(LicenseActivationRequest request, User authenticatedUser);
    Ticket renewLicense(UpdateLicenseRequest updateLicenseRequest, User authenticatedUser);
    ResponseEntity<?> getLicenseInfo(@RequestParam String mac);
    long countActiveDevicesForLicense(License license);
    void deleteById(Long id);
    void changeLicenseStatus(Long licenseId, boolean isBlocked, User authenticatedUser);
    boolean existsByProductId(Long id);
    boolean existsByLicenseTypeId(Long id);
}
