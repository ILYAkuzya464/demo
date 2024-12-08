package com.example.demo.services;

import com.example.demo.model.Device;
import com.example.demo.model.User;

public interface DeviceService {
    Device addDevice(String name, User user);
    Device getByMac(String mac);
}

