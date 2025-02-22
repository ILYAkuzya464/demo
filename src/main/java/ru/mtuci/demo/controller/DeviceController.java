package ru.mtuci.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.demo.controller.dto.AddDeviceRequest;
import ru.mtuci.demo.model.ApplicationRole;
import ru.mtuci.demo.model.Device;
import ru.mtuci.demo.model.User;
import ru.mtuci.demo.services.DeviceService;
import ru.mtuci.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

//TODO: 1. Дублирование кода между классами - getAuthenticatedUser() перенес в сервис юзера

@RequiredArgsConstructor
@RequestMapping("/device")
@RestController
public class DeviceController {

    private final DeviceService deviceService;
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<String> addDevice(@RequestBody AddDeviceRequest addDeviceRequest) {
        try {
            User user = userService.getAuthenticatedUser();
            deviceService.addDevice(addDeviceRequest.getName(), user, addDeviceRequest.getMac());
            return ResponseEntity.ok("Устройство успешно добавлено");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{deviceId}")
    public ResponseEntity<String> deleteDevice(@PathVariable Long deviceId) {
        try {
            deviceService.deleteDevice(deviceId);
            return ResponseEntity.ok("Устройство успешно удалено");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/view")
    public ResponseEntity<?> getDevicesForAuthenticatedUser() {
        try {
            User user = userService.getAuthenticatedUser();
            List<Device> devices = deviceService.getDevicesByUser(user);
            if (devices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("У пользователя нет устройств");
            }
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }
}
