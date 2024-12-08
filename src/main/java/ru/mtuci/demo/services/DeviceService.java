package ru.mtuci.demo.services;

import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.User;

public interface DeviceService {
    Device addDevice(String name, User user);
    Device getByMac(String mac);
}

