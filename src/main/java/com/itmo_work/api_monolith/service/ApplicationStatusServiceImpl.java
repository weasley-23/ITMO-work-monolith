package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.model.ApplicationStatus;
import com.itmo_work.api_monolith.model.ApplicationStatusName;
import com.itmo_work.api_monolith.repository.ApplicationStatusRepository;
import com.itmo_work.api_monolith.service.interfaces.ApplicationStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ApplicationStatusServiceImpl implements ApplicationStatusService {
    private final ApplicationStatusRepository applicationStatusRepository;

    @Override
    public Optional<ApplicationStatus> findApplicationStatusByApplicationStatusName(ApplicationStatusName applicationStatusName) {
        return applicationStatusRepository.findByApplicationStatus(applicationStatusName);
    }
}
