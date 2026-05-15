package com.itmo_work.api_monolith.service;

import com.itmo_work.api_monolith.model.CompanyStatus;
import com.itmo_work.api_monolith.model.CompanyStatusName;
import com.itmo_work.api_monolith.repository.CompanyStatusRepository;
import com.itmo_work.api_monolith.service.interfaces.CompanyStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyStatusServiceImpl implements CompanyStatusService {

    private final CompanyStatusRepository companyStatusRepository;

    @Override
    public Optional<CompanyStatus> findCompanyStatusByCompanyStatusName(CompanyStatusName companyStatusName) {
        return companyStatusRepository.findCompanyStatusByStatusName(companyStatusName);
    }
}
