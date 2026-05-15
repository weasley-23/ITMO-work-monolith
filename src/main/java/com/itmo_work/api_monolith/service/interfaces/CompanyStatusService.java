package com.itmo_work.api_monolith.service.interfaces;

import com.itmo_work.api_monolith.model.CompanyStatus;
import com.itmo_work.api_monolith.model.CompanyStatusName;

import java.util.Optional;

public interface CompanyStatusService {

    Optional<CompanyStatus> findCompanyStatusByCompanyStatusName(CompanyStatusName companyStatusName);
}
