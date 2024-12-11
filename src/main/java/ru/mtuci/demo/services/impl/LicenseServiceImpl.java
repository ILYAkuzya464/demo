package ru.mtuci.demo.services.impl;

import ru.mtuci.demo.model.*;
import ru.mtuci.demo.repo.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.controller.dto.LicenseActivationRequest;
import ru.mtuci.demo.controller.dto.UpdateLicenseRequest;
import ru.mtuci.demo.model.*;
import ru.mtuci.demo.repo.DeviceLicenseRepository;
import ru.mtuci.demo.repo.LicenseRepository;
import ru.mtuci.demo.services.*;
import ru.mtuci.demo.services.*;
import ru.mtuci.demo.ticket.Ticket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

//TODO: 1. Как пользователю активировать лицензию на новом устройстве, если вы кидаете исключение?
//TODO: 2. Тексты исключений не соответствуют реальной логике
//TODO: 3. Ошибки в подсчетах дат
//TODO: 4. Для чего нужен список тикетов?

@RequiredArgsConstructor
@Service
public class LicenseServiceImpl implements LicenseService {

    private final LicenseRepository licenseRepository;
    private final ProductService productService;
    private final UserService userService;
    private final DeviceService deviceService;
    private final LicenseTypeService licenseTypeService;
    private final LicenseHistoryService licenseHistoryService;
    private final DeviceLicenseRepository deviceLicenseRepository;
    private final DeviceRepository deviceRepository;

    @Override
    public License createLicense(Long productId, Long ownerId, Long licenseTypeId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Продукт не найден");
        }

        User user = userService.getById(ownerId);
        if (user == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        LicenseType licenseType = licenseTypeService.getLicenseTypeById(licenseTypeId);
        if (licenseType == null) {
            throw new NoSuchElementException("Тип лицензии не найден");
        }

        License license = new License();
        license.setProduct(product);
        license.setOwner(user);
        license.setLicenseType(licenseType);
        license.setMaxDevices(licenseType.getMaxDevices());

        String activationCode;
        do {
            activationCode = UUID.randomUUID().toString();
        } while (licenseRepository.existsByKey(activationCode));
        license.setKey(activationCode);

        licenseRepository.save(license);

        licenseHistoryService.recordLicenseChange(license, user, "Создана", "Лицензия успешно создана");

        return license;
    }

    @Override
    public Ticket activateLicense(LicenseActivationRequest request, User authenticatedUser) {

        Device device = deviceRepository.findByMac(request.getMac())
                .orElseThrow(() -> new IllegalArgumentException("Устройство с таким MAC-адресом не найдено"));

        License license = getByKey(request.getKey());
        if (license == null) {
            throw new IllegalArgumentException("Лицензия не найдена");
        }

        if (license.getUser()==null){
            license.setUser(authenticatedUser);
        }

        if (!license.getUser().getId().equals(authenticatedUser.getId())) {
            throw new IllegalArgumentException("Устройство не принадлежит текущему пользователю");
        }

        long activeDeviceCount = countActiveDevicesForLicense(license);
        if (activeDeviceCount >= license.getMaxDevices()) {
            throw new IllegalArgumentException("Превышено максимальное количество устройств для данной лицензии");
        }

        boolean isAlreadyLinked = deviceLicenseRepository
                .findByLicenseIdAndDeviceId(license.getId(), device.getId())
                .isPresent();
        if (isAlreadyLinked) {
            throw new IllegalArgumentException("Устройство уже связано с этой лицензией");
        }

        DeviceLicense deviceLicense = new DeviceLicense();
        deviceLicense.setLicense(license);
        deviceLicense.setDevice(device);
        deviceLicense.setActivationDate(new Date());
        deviceLicenseRepository.save(deviceLicense);

        int defaultDuration = license.getLicenseType().getDefaultDuration();
        LocalDate expirationDate = LocalDate.now().plusMonths(defaultDuration);
        Date newExpiration = Date.from(expirationDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (license.getExpirationDate() == null) {
            license.setExpirationDate(newExpiration);
        }
        license.setActivationDate(new Date());
        license.setBlocked(false);

        licenseRepository.save(license);

        licenseHistoryService.recordLicenseChange(license, authenticatedUser, "Активировано", "Лицензия успешно активирована");

        Ticket ticket = new Ticket(license, device);
        return ticket;
    }

    public Ticket renewLicense(UpdateLicenseRequest updateLicenseRequest, User authenticatedUser) {
        boolean isAdmin = authenticatedUser.getRole() == ApplicationRole.ADMIN;

        License oldLicense = getByKey(updateLicenseRequest.getOldLicenseKey());
        if (oldLicense == null) {
            throw new IllegalArgumentException("Активная лицензия не найдена по указанному ключу");
        }

        User licenseOwner = oldLicense.getUser();
        if (!isAdmin && (licenseOwner == null || !licenseOwner.getEmail().equals(authenticatedUser.getEmail()))) {
            throw new IllegalArgumentException("Вы не можете продлевать чужую лицензию");
        }

        License newLicense = getByKey(updateLicenseRequest.getLicenseKey());
        if (newLicense == null) {
            throw new IllegalArgumentException("Новая лицензия не найдена по указанному ключу");
        }
        if(oldLicense.getBlocked()==true)
        {
            throw new IllegalArgumentException("Лицензия заблокирована");
        }


        if (newLicense.getActivationDate() != null) {
            throw new IllegalArgumentException("Новая лицензия уже активирована");
        }

        Integer oldMaxDevices = oldLicense.getLicenseType().getMaxDevices();
        Integer newMaxDevices = newLicense.getLicenseType().getMaxDevices();
        if (oldMaxDevices > newMaxDevices) {
            throw new IllegalArgumentException("Нельзя продлить лицензию, так как текущая лицензия выше классом.");
        }

        long activeDeviceCount = countActiveDevicesForLicense(newLicense);
        if (activeDeviceCount >= newMaxDevices) {
            throw new IllegalArgumentException("Слишком много устройств");
        }

        Integer durationMonths = newLicense.getLicenseType().getDefaultDuration();
        Date calculatedExpiration;
        calculatedExpiration = Date.from(
                oldLicense.getExpirationDate()
                        .toInstant()
                        .atOffset(ZoneOffset.UTC)
                        .toLocalDate()
                        .plusMonths(durationMonths)
                        .atTime(0, 0)
                        .toInstant(ZoneOffset.UTC)
        );

        List<DeviceLicense> deviceLicenses = deviceLicenseRepository.findByLicenseId(oldLicense.getId());
        for (DeviceLicense dl : deviceLicenses) {
            dl.setLicense(newLicense);
            deviceLicenseRepository.save(dl);
        }
        licenseRepository.delete(oldLicense);

        newLicense.setUser(licenseOwner);
        newLicense.setBlocked(false);
        newLicense.setActivationDate(new Date());
        newLicense.setExpirationDate(calculatedExpiration);
        add(newLicense);

        licenseHistoryService.recordLicenseChange(
                newLicense,
                licenseOwner,
                "Продлено",
                "Новая дата окончания: " + calculatedExpiration
        );

        Ticket ticket = new Ticket(newLicense, deviceLicenses.get(0).getDevice());
        return ticket;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getLicenseInfo(@RequestParam String mac) {
        try {
            Device device = deviceService.getByMac(mac);
            if (device == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Устройство не найдено");
            }

            List<DeviceLicense> deviceLicenses = deviceLicenseRepository.findByDeviceId(device.getId());

            List<Ticket> tickets = new ArrayList<>();
            deviceLicenses.stream()
                    .map(DeviceLicense::getLicense) // Извлекаем лицензии из списка
                    .filter(Objects::nonNull) // Отбрасываем null значения
                    .filter(license -> license.getExpirationDate() == null ||
                            !license.getExpirationDate().before(new Date())) // Проверяем срок действия лицензии
                    .map(license -> new Ticket(license, device)) // Создаем новые объекты Ticket
                    .forEach(tickets::add); // Добавляем их в список tickets


            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    public void changeLicenseStatus(Long licenseId, boolean isBlocked, User authenticatedUser) {
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("Лицензия с таким ID не найдена"));

        boolean previousStatus = license.getBlocked();

        if (previousStatus != isBlocked) {
            license.setBlocked(isBlocked);
            licenseRepository.save(license);

            String action = isBlocked ? "Блокировка" : "Разблокировка";
            String description = "Лицензия была " + (isBlocked ? "заблокирована" : "разблокирована");

            licenseHistoryService.recordLicenseChange(
                    license,
                    authenticatedUser,
                    action,
                    description
            );
        }
    }

    @Override
    public void add(License license) {
        licenseRepository.save(license);
    }
    @Override
    public boolean existsByLicenseTypeId(Long licenseTypeId) {
        return licenseRepository.existsByLicenseTypeId(licenseTypeId);
    }
    @Override
    public boolean existsByProductId(Long productId) {
        return licenseRepository.existsByProductId(productId);
    }
    @Override
    public License findById(Long id) {
        return licenseRepository.findById(id).orElse(null);
    }
    @Override
    public void deleteById(Long id) {
        licenseRepository.deleteById(id);
    }

    @Override
    public License getByKey(String key) {
        return licenseRepository.findByKey(key).orElse(null);
    }

    public long countActiveDevicesForLicense(License license) {
        return deviceLicenseRepository.countByLicenseAndActivationDateIsNotNull(license);
    }

}
