package com.itmo_work.api_monolith.repository;

import com.itmo_work.api_monolith.model.CompanyStatus;
import com.itmo_work.api_monolith.model.CompanyStatusName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyStatusRepository extends JpaRepository<CompanyStatus, Long> {

    Optional<CompanyStatus> findCompanyStatusByStatusName(CompanyStatusName companyStatusName);
}
