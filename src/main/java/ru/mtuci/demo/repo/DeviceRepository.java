package ru.mtuci.demo.repo;

import ru.mtuci.demo.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByMac(String mac);
}
