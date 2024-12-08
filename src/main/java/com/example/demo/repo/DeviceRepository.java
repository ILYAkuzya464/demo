package com.example.demo.repo;

import com.example.demo.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByMac(String mac);
}
