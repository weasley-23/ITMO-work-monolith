package com.itmo_work.api_monolith.repository;

import com.itmo_work.api_monolith.model.ApplicationStatus;
import com.itmo_work.api_monolith.model.ApplicationStatusName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, Long> {
    Optional<ApplicationStatus> findByApplicationStatus(ApplicationStatusName applicationStatus);
}
