package ru.mtuci.demo.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.License;
import ru.mtuci.demo.model.LicenseHistory;
import ru.mtuci.demo.model.User;
import ru.mtuci.demo.repo.LicenseHistoryRepository;
import ru.mtuci.demo.repo.LicenseRepository;
import ru.mtuci.demo.services.LicenseHistoryService;

import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Service
public class LicenseHistoryServiceImpl implements LicenseHistoryService {

    private final LicenseHistoryRepository licenseHistoryRepository;
    private final LicenseRepository licenseRepository;

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
    @Override
    public List<LicenseHistory> getHistoryByLicenseKey(String licenseKey) {
        License license = licenseRepository.findByKey(licenseKey)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия с данным ключом не найдена"));
        return licenseHistoryRepository.findByLicenseId(license.getId());
    }
    @Override
    public void deleteAllHistory() {
        licenseHistoryRepository.deleteAll();
    }

    public void deleteHistoryByLicenseKey(String licenseKey) {
        License license = licenseRepository.findByKey(licenseKey)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия с данным ключом не найдена"));

        licenseHistoryRepository.deleteByLicenseId(license.getId());
    }
}
