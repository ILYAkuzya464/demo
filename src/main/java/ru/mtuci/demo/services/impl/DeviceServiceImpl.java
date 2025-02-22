package ru.mtuci.demo.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.User;
import ru.mtuci.demo.repo.DeviceRepository;
import ru.mtuci.demo.services.DeviceService;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

// Откуда берётся мак-адрес устройства? -  теперь мак отправляю через запрос

@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    public Device addDevice(String name, User user, String mac) {
        Optional<Device> existingDevice = deviceRepository.findByMac(mac);
        if (existingDevice.isPresent()) {
            throw new IllegalArgumentException("Устройство с таким MAC-адресом уже существует");
        }

        Device device = new Device();
        device.setName(name);
        device.setMac(mac);
        device.setUser(user);
        return deviceRepository.save(device);
    }


    public Device getByMac(String mac) {
        return deviceRepository.findByMac(mac)
                .orElseThrow(() -> new EntityNotFoundException("Устройство с MAC-адресом " + mac + " не найдено"));
    }

    public void deleteDevice(Long deviceId) {
        deviceRepository.deleteById(deviceId);
    }
    public List<Device> getDevicesByUser(User user) {
        return deviceRepository.findByUser(user);
    }

}