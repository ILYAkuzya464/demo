package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.License;
import com.example.demo.model.Product;
import com.example.demo.model.User;

import java.util.Date;
import java.util.List;
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
