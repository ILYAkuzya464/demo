package ru.mtuci.demo.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateLicenseRequest {
    private String licenseKey;
    private String oldLicenseKey;
}