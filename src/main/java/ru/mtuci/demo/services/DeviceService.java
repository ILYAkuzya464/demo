package ru.mtuci.demo.services;

import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.User;

import java.util.List;

public interface DeviceService {
    Device addDevice(String name, User user, String mac);
    Device getByMac(String mac);
    void deleteDevice(Long deviceId);

    List<Device> getDevicesByUser(User user);
}

