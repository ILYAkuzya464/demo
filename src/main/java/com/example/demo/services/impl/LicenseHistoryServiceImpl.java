package com.example.demo.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.License;
import com.example.demo.model.LicenseHistory;
import com.example.demo.model.User;
import com.example.demo.repo.LicenseHistoryRepository;
import com.example.demo.services.LicenseHistoryService;

import java.util.Date;


@Service
public class LicenseHistoryServiceImpl implements LicenseHistoryService {

    @Autowired
    private LicenseHistoryRepository licenseHistoryRepository;

    @Override
    public void recordLicenseChange(License license, User user, String status, String description) {
        LicenseHistory history = new LicenseHistory();
        history.setLicense(license);
        history.setUser(user);
        history.setStatus(status);
        history.setDescription(description);
        history.setChangeDate(new Date());

        licenseHistoryRepository.save(history);
    }
}
