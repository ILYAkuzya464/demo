package com.example.demo.services;

import com.example.demo.model.License;
import com.example.demo.model.User;

public interface LicenseHistoryService {
    void recordLicenseChange(License license, User user, String status, String description);
}
