package ru.mtuci.demo.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LicenseActivationRequest {
    private String key;
    private String mac;
}
