package ru.mtuci.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mtuci.demo.model.License;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findByKey(String key);
    boolean existsByKey(String key);
    void delete(License license);
    boolean existsByProductId(Long productId);
    boolean existsByLicenseTypeId(Long licenseTypeId);
    boolean existsByUserId(Long userId);
}
