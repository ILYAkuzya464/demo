package ru.mtuci.demo.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.LicenseType;
import ru.mtuci.demo.repo.LicenseRepository;
import ru.mtuci.demo.repo.LicenseTypeRepository;
import ru.mtuci.demo.services.LicenseTypeService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LicenseTypeServiceImpl implements LicenseTypeService {

    private final LicenseTypeRepository licenseTypeRepository;
    private final LicenseRepository licenseRepository;

    public void deleteById(Long id) {
        licenseTypeRepository.deleteById(id);
    }

    @Override
    public LicenseType getLicenseTypeById(Long id) {
        return licenseTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Тип лицензии с ID " + id + " не найден"));
    }

    @Override
    public LicenseType addLicenseType(LicenseType licenseType) {
        if (licenseTypeRepository.existsByName(licenseType.getName())) {
            throw new IllegalArgumentException("Тип лицензии с таким именем уже существует");
        }

        return licenseTypeRepository.save(licenseType);
    }

    @Override
    public void updateDuration(Long licenseTypeId, int newDefaultDuration) {
        boolean existsLicenses = licenseRepository.existsByLicenseTypeId(licenseTypeId);
        if (existsLicenses) {
            throw new IllegalArgumentException("Невозможно изменить длительность, так как для данного типа лицензий уже существуют лицензии.");
        }

        LicenseType licenseType = licenseTypeRepository.findById(licenseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Тип лицензии не найден"));

        licenseType.setDefaultDuration(newDefaultDuration);
        licenseTypeRepository.save(licenseType);
    }

    @Override
    public List<LicenseType> findAll(){
        return licenseTypeRepository.findAll();
    }

    @Override
    public Optional<LicenseType> findById(Long id){
        return licenseTypeRepository.findById(id);
    }

}
