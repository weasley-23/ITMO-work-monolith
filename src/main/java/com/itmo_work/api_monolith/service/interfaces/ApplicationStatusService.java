package com.itmo_work.api_monolith.service.interfaces;

import com.itmo_work.api_monolith.model.ApplicationStatus;
import com.itmo_work.api_monolith.model.ApplicationStatusName;

import java.util.Optional;

public interface ApplicationStatusService {
    Optional<ApplicationStatus> findApplicationStatusByApplicationStatusName(ApplicationStatusName applicationStatusName);
}
