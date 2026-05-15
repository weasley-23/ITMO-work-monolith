package com.itmo_work.api_monolith.repository;

import com.itmo_work.api_monolith.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByEmail(String email);
    boolean existsByIdAndUsers_Id(Long companyId, Long usersId);
}