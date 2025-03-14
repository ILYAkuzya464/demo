package ru.mtuci.demo.services;

import ru.mtuci.demo.model.LicenseType;

import java.util.List;
import java.util.Optional;

public interface LicenseTypeService {
    LicenseType getLicenseTypeById(Long id);
    LicenseType addLicenseType(LicenseType licenseType);
    List<LicenseType> findAll();
    Optional<LicenseType> findById(Long id);
    void deleteById(Long id);
    void updateDuration(Long licenseTypeId, int newDefaultDuration);
}
