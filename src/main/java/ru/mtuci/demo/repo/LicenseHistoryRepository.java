package ru.mtuci.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.model.LicenseHistory;

public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
}
