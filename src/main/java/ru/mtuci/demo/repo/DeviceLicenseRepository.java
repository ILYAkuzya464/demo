package ru.mtuci.demo.repo;

import ru.mtuci.demo.model.DeviceLicense;
import ru.mtuci.demo.model.License;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
    Optional<DeviceLicense> findByLicenseIdAndDeviceId(Long licenseId, Long deviceId);
    List<DeviceLicense> findByDeviceId(Long deviceId);
    long countByLicenseAndActivationDateIsNotNull(License license);
    List<DeviceLicense> findByLicenseId(Long licenseId);
}
