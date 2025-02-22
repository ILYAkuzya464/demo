package ru.mtuci.demo.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AddDeviceRequest {
    String name;
    String mac;
}
