package ru.mtuci.demo.services;

import ru.mtuci.demo.controller.dto.LicenseActivationRequest;
import ru.mtuci.demo.controller.dto.UpdateLicenseRequest;
import ru.mtuci.demo.model.License;
import ru.mtuci.demo.model.User;
import ru.mtuci.demo.ticket.Ticket;
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
