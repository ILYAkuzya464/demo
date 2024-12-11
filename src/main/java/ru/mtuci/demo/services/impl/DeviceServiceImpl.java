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
import java.util.Optional;

//TODO: 1. Откуда берётся мак-адрес устройства?

@RequiredArgsConstructor
@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    public Device addDevice(String name, User user) {
        String mac = getLocalMacAddress();
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

    public static String getLocalMacAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                if (networkInterface.isUp() && !networkInterface.isVirtual()) {
                    byte[] macAddress = networkInterface.getHardwareAddress();
                    if (macAddress != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < macAddress.length; i++) {
                            sb.append(String.format("%02X", macAddress[i]));
                            if (i < macAddress.length - 1) sb.append(":");
                        }
                        return sb.toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Device getByMac(String mac) {
        return deviceRepository.findByMac(mac)
                .orElseThrow(() -> new EntityNotFoundException("Устройство с MAC-адресом " + mac + " не найдено"));
    }

}