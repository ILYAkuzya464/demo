package com.example.demo.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.model.LicenseType;
import com.example.demo.repo.LicenseTypeRepository;
import com.example.demo.services.LicenseTypeService;

import java.util.List;
import java.util.Optional;

@Service
public class LicenseTypeServiceImpl implements LicenseTypeService {

    @Autowired
    private LicenseTypeRepository licenseTypeRepository;

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
    public List<LicenseType> findAll(){
        return licenseTypeRepository.findAll();
    }

    @Override
    public Optional<LicenseType> findById(Long id){
        return licenseTypeRepository.findById(id);
    }

}
