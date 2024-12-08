package com.example.demo.services;

import com.example.demo.model.LicenseType;

import java.util.List;
import java.util.Optional;

public interface LicenseTypeService {
    LicenseType getLicenseTypeById(Long id);
    LicenseType addLicenseType(LicenseType licenseType);
    List<LicenseType> findAll();
    Optional<LicenseType> findById(Long id);
    void deleteById(Long id);
}
