package ru.mtuci.demo.repo;

import ru.mtuci.demo.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.model.User;


import java.util.List;
import java.util.Optional;


public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByMac(String mac);

    List<Device> findByUser(User user);

}
